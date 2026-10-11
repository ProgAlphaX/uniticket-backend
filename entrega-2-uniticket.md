# Entrega 2 — UniTicket: Plan de trabajo en equipo (JPA + PostgreSQL + JWT)

## Qué cambia en esta entrega

Pasamos de guardar todo en memoria (`Map` + `AtomicLong`) a PostgreSQL real (Neon) vía JPA, y agregamos login con JWT con usuarios validados desde la base de datos. Se mantienen los 8 endpoints actuales + se agrega 1 nuevo de login. Siguen vigentes las reglas de siempre: nombres de campo `nId_X`/`sX`/`dX`, DTOs como `record`, sin Lombok, mismo estilo de tests (`@SpringBootTest` + `@AutoConfigureMockMvc` + `assertEquals`).

Rúbrica a cumplir: 8 operaciones REST con acceso real a BD (2+ controllers) · JWT con usuarios desde BD · mínimo 2 casos de prueba por servicio.

---

## Julio — Infraestructura, seguridad y JWT

Julio deja listo el terreno antes de que el equipo empiece:
- Base de datos en Neon + `application.properties` configurado con las credenciales reales.
- `build.gradle` con **todas** las dependencias nuevas de una sola vez (JPA, Security, PostgreSQL, JWT) — nadie más debe tocar este archivo.
- Carpetas `repository/`, `security/` creadas (esqueleto) antes de repartir.
- Toda la capa `security/`: `JWTUtil`, `JWTFilter`, `WebSecurityConfig`, `MyUserDetailsService`, y el endpoint de login (`AutenticacionController` + `LoginRequestDTO`), adaptada al `Usuario` que ya existe en el proyecto (reutiliza el campo `sRol` como autoridad, sin agregar una entidad `Rol` ni tabla intermedia aparte).
- Los 2 handlers nuevos en `GlobalExceptionHandler` para credenciales inválidas (401) y usuario inactivo (401).
- **No hay seed automático.** La base nace vacía. Después del primer `bootRun` (que crea las tablas), Julio inserta a mano en el editor SQL de Neon **un primer usuario con rol `ADMIN`** (password ya hasheada con BCrypt) — es el único dato que se crea manualmente, porque hace falta alguien ya logueado para poder registrar al resto vía la API (todo `/api/**` exige JWT, incluyendo `POST /api/usuarios`). Comparte el email/password de ese admin con el equipo — Diego lo necesita para los tests de login.

---

## Luis — Usuario

**Objetivo:** que Usuario funcione contra base de datos real en vez de memoria.

**Realizado** - Convertir `model/Usuario.java` en entidad JPA: `@Entity`, `@Table`, `@Column` en cada campo, `@Id` con `@GeneratedValue(strategy = GenerationType.IDENTITY)`. **No cambies los nombres de los campos** (`nId_Usuario`, `sNombreCompleto`, etc.).
**Realizado** - Crear `repository/UsuarioRepository.java` (interface que extiende `JpaRepository<Usuario, Long>`) con métodos para: buscar por email, saber si un email ya existe, listar por estado, y buscar por nombre (contiene, sin importar mayúsculas).
- ⚠️ **Ojo con los nombres de los métodos derivados**: como los campos llevan prefijo (`sEstado`, `sEmail`), Spring Data necesita el método escrito como `findBysEstado(...)` (la "s" en minúscula pegada al "By"), no `findBySEstado`. Si algo no compila o tira `PropertyReferenceException` al arrancar, probablemente es por esto — avísame.
- Reescribir `UsuarioService` para que use el repository en vez del `Map`. Cada método actual (`listarUsuarios`, `buscarPorNombre`, `crearUsuario`, `actualizarUsuario`) debe seguir devolviendo exactamente lo mismo que hoy, solo que leyendo/escribiendo en la BD.
- Agregar el campo `sPassword` a `UsuarioCreateDTO` (con `@NotBlank` y `@Size(min = 6)`). En `crearUsuario`, guarda el password hasheado usando el `PasswordEncoder` que Julio deja como bean — solo inyéctalo en el constructor del service.

⚠️ `TicketService`, `TicketController` y la carpeta `security/` las está trabajando otra persona en paralelo — evita tocarlos para no generar conflictos.

---

## Grover — Ticket

**Objetivo:** que Ticket funcione contra base de datos real.

- Convertir `model/Ticket.java` en entidad JPA, mismo criterio que Usuario (sin cambiar nombres de campo).
- Crear `repository/TicketRepository.java` — no necesita métodos custom, con lo que trae `JpaRepository` (`findAll`, `findById`, `save`) alcanza.
- Reescribir en `TicketService` **solo** estos 3 métodos: `crearTicket`, `listarTickets`, `obtenerTicketPorId`. ⚠️ `resolverTicket` lo hace Alexander porque depende de la entidad Resolución.
  - `crearTicket`: antes de guardar, verifica que el usuario exista (`usuarioRepository.existsById`), si no existe lanza `ResourceNotFoundException`. El número de ticket (`SOL-000X`) se arma **después** de guardar, usando el id que genera la base de datos.
  - `listarTickets`: mantén las mismas 2 validaciones de catálogo (estado y tipo) que ya existen, filtrando sobre lo que devuelve `findAll()`.
  - `obtenerTicketPorId`: usa `findById(...).orElseThrow(...)` para el 404.

⚠️ `UsuarioService`, `resolverTicket` y `security/` los está trabajando otra persona en paralelo — evita tocarlos para no generar conflictos.

---

## Alexander — Resolución de tickets

**Objetivo:** hacer que la Resolución de un ticket se guarde como una tabla real en la base de datos (hoy no existe como tabla, solo vive de forma temporal en memoria) y conectarla con el ticket resuelto.

Como esta parte es nueva (crear una entidad desde cero y relacionarla con otra), va paso a paso:

1. **Espera a que Grover termine `Ticket` como `@Entity`** antes de empezar — lo necesitas para poder relacionar tu entidad nueva con `Ticket`.
2. **Entiende la relación antes de programarla:** cada ticket resuelto tiene como máximo **una** resolución, y cada resolución pertenece a un solo ticket. Eso es una relación "1 a 1", y en JPA se declara con la anotación `@OneToOne`.
3. **Crea `model/Resolucion.java`** como una clase nueva, con el mismo estilo que `Usuario`/`Ticket` (`@Entity`, `@Table(name = "resolucion")`, `@Column` en cada campo, constructor vacío + constructor con todos los campos + getters/setters a mano, sin Lombok). Necesita estos campos:
   - `nId_Resolucion` — el id propio de la resolución (`@Id @GeneratedValue(strategy = GenerationType.IDENTITY)`).
   - `ticket` — de tipo `Ticket`, con `@OneToOne` y `@JoinColumn(name = "n_id_ticket", nullable = false, unique = true)`. Esta anotación crea en la tabla `resolucion` una columna que apunta al id del ticket (el `unique = true` es lo que obliga a que un ticket no pueda tener dos resoluciones).
   - `nId_Admin` — el id del administrador que respondió (campo simple, no relación).
   - `sTexto_Respuesta` — el texto de la respuesta.
   - `dFecha_Respuesta` — cuándo se respondió.
4. **Crea `repository/ResolucionRepository.java`** — interface que extiende `JpaRepository<Resolucion, Long>`, con un método para encontrar la resolución de un ticket dado (algo como `findByTicket(Ticket ticket)`).
5. **Reescribe `resolverTicket` en `TicketService`** con esta lógica, en orden:
   - Buscar el ticket por id; si no existe, 404 ("Ticket no encontrado").
   - Buscar al usuario admin por `nId_Admin`; si no existe, 404 ("Administrador no encontrado").
   - Verificar que ese usuario tenga `sRol == "ADMIN"`; si no, 400 ("El usuario indicado no es administrador").
   - Cambiar el estado del ticket a `RESUELTO` y guardarlo.
   - Buscar si el ticket ya tenía una resolución (con el método del repository del paso 4); si tenía, actualízala; si no, crea una nueva. Guárdala.
6. **Avísale a Diego qué mensaje exacto devuelves en cada validación nueva** (admin no encontrado, admin sin rol ADMIN) — él va a escribir los tests para esos 2 casos nuevos (ver su sección abajo), y necesita saber el texto/código exacto para verificarlo.

⚠️ `UsuarioService`, los métodos `crearTicket`/`listarTickets`/`obtenerTicketPorId` y `security/` los está trabajando otra persona en paralelo — evita tocarlos para no generar conflictos.

---

## Diego — Tests

**Objetivo:** adaptar los tests actuales a que funcionen contra base de datos real (sin datos semilla) + escribir los tests del login.

Espera a que los demás terminen su parte y la app corra sin errores antes de empezar — necesitas la app completa funcionando.

⚠️ **Importante — ya no hay datos de ejemplo garantizados.** La base no se llena sola al arrancar: solo tiene el admin que Julio crea a mano y lo que cada test vaya registrando. Esto significa que los tests actuales (`UsuarioControllerTest`, `TicketControllerTest`), que hoy asumen cosas como "existe Ana Torres" o "el ticket 1 es SOL-0001", ya no pueden depender de eso. Hay que revisarlos uno por uno y, en cada test que lo necesite, **crear primero el dato con un `POST` dentro del mismo test**, y recién después hacer la verificación sobre ese dato que tú mismo creaste (no sobre datos fijos esperados de antemano).

Hay dos estilos distintos de test en este proyecto, según qué estén probando:

**1. Tests de lógica de negocio** (`UsuarioControllerTest`, `TicketControllerTest`) — prueban que el CRUD funciona, no la seguridad en sí:
- Agrégales a nivel de clase `@Transactional` (revierte todo lo que el test escribió en la BD al terminar, así no se acumulan datos de prueba) y `@WithMockUser` (simula una sesión autenticada, para que el filtro JWT no te bloquee mientras pruebas lógica de negocio).
- Con `@WithMockUser` puesto, puedes hacer `POST /api/usuarios` o `POST /api/tickets` libremente dentro de cada test para crear los datos que necesites, sin tener que loguearte de verdad.
- En los tests que crean un usuario, agrega el campo `"sPassword"` al JSON, porque ahora es obligatorio.
- Agrega 2 tests nuevos en `TicketControllerTest` para las validaciones de admin que agrega Alexander en `resolverTicket` (coordina con él el mensaje/código exacto): resolver un ticket mandando un `nId_Admin` que no existe → 404; resolver un ticket mandando un `nId_Admin` que sí existe pero no tiene rol `ADMIN` → 400.

**2. Tests de autenticación** (`AutenticacionControllerTest`, nueva) — prueban el login real, así que **no** llevan `@WithMockUser` (eso falsearía justo lo que se está probando). Como `POST /api/usuarios` también exige JWT, estos tests necesitan arrancar logueándose con el admin que Julio creó a mano en Neon (el único usuario garantizado que existe siempre), y desde ahí, si necesitan otro usuario (por ejemplo uno `INACTIVO` para probar que no puede loguearse), lo crean con el token de ese admin dentro del mismo test — también con `@Transactional` para que no quede nada de más en la base real.

Mismo estilo de siempre en ambos casos: `@SpringBootTest` + `@AutoConfigureMockMvc` + `MockMvc` autowireado + `MvcResult` + `assertEquals`/`assertTrue` + `System.out.println("Ejecutando ...")`.

⚠️ **Todos los tests corren contra la base de datos real de Neon** (no hay una base aparte solo para pruebas). Por eso el `@Transactional` en cada clase de test no es opcional: es lo que evita que las pruebas ensucien permanentemente la base real al revertir cada escritura apenas termina el test.

### Lista completa de los 24 casos actuales que hay que migrar a BD real, + 3 nuevos de login

El profesor pide mínimo 2 casos de prueba por servicio REST, pero este proyecto ya tiene **3 casos por cada uno de los 8 servicios (24 en total)** — ninguno se reduce, todos se migran para que corran contra la base de datos real (creando sus propios datos con `POST` dentro del test, como se explicó arriba). Además se agregan exactamente **3 casos nuevos para el login**. Total: **27 casos**.

**Usuario — 12 casos (4 servicios × 3)**

| Servicio | Caso 1 | Caso 2 | Caso 3 |
|---|---|---|---|
| `POST /api/usuarios` | Datos válidos (incluye `sPassword`) → 201, estado `ACTIVO` | Email con formato inválido → 400 | Rol que no existe en el catálogo → 400 |
| `GET /api/usuarios` | Sin filtro → 200, lista | `?estado=ACTIVO` → 200, solo esos | `?estado=NOEXISTE` → 400 |
| `GET /api/usuarios/buscar` | Nombre con coincidencia (de un usuario creado en el test) → 200, aparece | Nombre sin coincidencia → 200, lista vacía | Nombre insensible a mayúsculas → 200, igual aparece |
| `PUT /api/usuarios/{id}` | Datos válidos sobre un id creado en el test → 200 | Id que no existe → 404 | Estado inválido en el body → 400 |

**Ticket — 12 casos (4 servicios × 3)**

| Servicio | Caso 1 | Caso 2 | Caso 3 |
|---|---|---|---|
| `POST /api/tickets` | Datos válidos, usuario existente → 201, estado `PENDIENTE` | Descripción vacía → 400 | Sin `nId_Usuario` → 400 |
| `GET /api/tickets` | Sin filtro → 200, lista | `?estado=RESUELTO` → 200, solo esos | `?estado=NOEXISTE` → 400 |
| `GET /api/tickets/{id}` | Id de un ticket creado en el test → 200, datos correctos | Id que no existe → 404 | Ticket recién creado, sin resolución aún → 200, `resolucion: null` |
| `PUT /api/tickets/{id}/respuesta` | Datos válidos, admin existente con rol `ADMIN` → 200, estado `RESUELTO` | Ticket que no existe → 404 | Texto de respuesta vacío → 400 |

A estos 3 de `resolverTicket` se suman los **2 adicionales** ya mencionados arriba por las validaciones nuevas de Alexander (admin inexistente → 404, admin sin rol `ADMIN` → 400) — esos van aparte, no reemplazan a los 3 de la tabla.

**Autenticación — 3 casos nuevos**

| Caso 1 | Caso 2 | Caso 3 |
|---|---|---|
| Login válido (email/password del admin de Julio) → 200, el cuerpo es un JWT (3 partes separadas por punto) | Login con password incorrecta → 401 | Login con un usuario `INACTIVO` (créalo y desactívalo dentro del mismo test usando el token del admin) → 401 |

⚠️ Trabaja solo sobre los archivos de test — la lógica de `UsuarioService`/`TicketService`/`security/` la están trabajando otras personas en paralelo.

---

## Flujo de Git para evitar conflictos

Como somos 5 trabajando en el mismo repo, sigan siempre este orden **antes** de empezar a programar cada sesión:

```bash
git status          # ver si tienes cambios locales sin commitear
git stash            # si tienes cambios locales, guárdalos temporalmente
git pull             # trae los últimos cambios del equipo
git stash apply      # si guardaste algo en el paso 2, lo recuperas aquí
```

Si `git stash apply` marca conflicto, resuélvanlo a mano revisando qué cambió antes de seguir — no hagan `git stash drop` sin haber confirmado que su cambio se aplicó bien.

Al terminar su parte:

```bash
git add .
git commit -m "mensaje claro de qué hiciste"
git pull             # por si alguien subió algo mientras tú trabajabas
git push
```

**Reglas:**
- Cada quien trabaja solo en los archivos de su parte (ver reparto arriba) — si necesitas tocar algo de otra persona, avísale primero.
- Commits pequeños y frecuentes, no uno gigante al final.
- Si `./gradlew test` no te pasa en tu máquina, no hagas push todavía — avisa.
