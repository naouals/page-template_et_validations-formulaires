
# Projet de Gestion des Patients avec Spring MVC

Ce projet est une application web développée avec Spring Boot, Spring Data JPA, Spring MVC et Thymeleaf. Elle permet de gérer une liste de patients avec les fonctionnalités suivantes : affichage, pagination, recherche, suppression, et amélioration de l'interface utilisateur avec Bootstrap. La base de données utilisée est H2 (en mémoire).
## Table des matières

1. [Configuration du projet](#configuration-du-projet)
2. [Structure du projet](#structure-du-projet)
4. [Fonctionnalités principales](#fonctionnalités-principales)
5. [Améliorations supplémentaires](#améliorations-supplémentaires)
6. [Partie du code explicative](#partie-du-code-explicative)

---

## Configuration du projet

### Prérequis

* Java JDK 21
* Maven
* IDE : IntelliJ Ultimate 


### Dépendances

Le projet inclut les dépendances suivantes dans le fichier `pom.xml` :

* `spring-boot-starter-data-jpa`
* `spring-boot-starter-web`
* `spring-boot-starter-thymeleaf`
* `h2` 
* `lombok`

### Fichier `application.properties`

```properties
spring.application.name=patients
server.port=8087
spring.datasource.url=jdbc:h2:mem:patients-db
spring.h2.console.enabled=true

```

## Structure du projet

```text
src/
├── main/
│   ├── java/
│   │   └── org.example.patients/
│   │       ├── entities/Patient.java
│   │       ├── repositories/PatientRepository.java
│   │       ├── controllers/PatientController.java
│   │       └── PatientsApplication.java
│   └── resources/
│       ├── templates/
│       │   ├── patients.html         ← Liste + recherche + pagination + suppression
│       │   └── formPatient.html      ← Formulaire d'ajout/modification
│       ├── static/
│       └── application.properties
pom.xml

```

---



## Fonctionnalités principales

### 1. Ajouter des patients

Depuis le code :

```java
@Bean
    CommandLineRunner start(PatientRepository patientRepository) {
        return args -> {
            patientRepository.save(new Patient(null, "Yasmine", "Berrada",new Date(), true, 120));
            patientRepository.save(new Patient(null, "Hamid", "Zahraoui",new Date(), false, 80));
            patientRepository.save(new Patient(null, "Sanae", "Abid",new Date(), true, 100));
        };
    }
```

### 2. Afficher tous les patients(dans une table)

Depuis `/index` :

```html
<table class="table table-hover table-bordered">
    <thead class="table-dark">
    <tr>
      <th>ID</th><th>Nom</th><th>Prénom</th><th>Date de naissance</th><th>Malade</th><th>Score</th><th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <tr th:each="p : ${listPatients}">
      <td th:text="${p.id}"></td>
      <td th:text="${p.nom}"></td>
      <td th:text="${p.prenom}"></td>
      <td th:text="${#dates.format(p.dateNaissance, 'yyyy-MM-dd')}"></td>
      <td th:text="${p.malade} ? 'Oui' : 'Non'"></td>
      <td th:text="${p.score}"></td>
      <td>
        <a class="btn btn-danger btn-sm"
           th:href="@{/delete(id=${p.id},page=${currentPage},keyword=${keyword})}"
           onclick="return confirm('Confirmer la suppression ?')">Supprimer</a>
        <a class="btn btn-warning btn-sm"
           th:href="@{/editPatient(id=${p.id})}">Modifier</a>
      </td>
    </tr>
    </tbody>
  </table>
```

![image](https://github.com/user-attachments/assets/7d397ba9-0a39-47d2-8efe-cc4114c0581d)


### 3. Rechercher des patients

```java
List<Patient> patients = patientRepository.findByNomContains("S");
```

![image](https://github.com/user-attachments/assets/c3b0da94-d2de-4ad6-9689-371c9f4c8da3)


### 4. Pagination des résultats

Gérée avec `PageRequest.of(page, size)` dans le contrôleur :

```java
Page<Patient> pagePatients = patientRepository.findByNomContains(keyword, PageRequest.of(page, size));
```

![image](https://github.com/user-attachments/assets/da8656fd-5132-4ee3-91fc-a3a06857cc85)


### 5. Supprimer un patient

Via lien dans la vue :

```html
<a th:href="@{/delete(id=${p.id}, keyword=${keyword}, page=${currentPage})}" class="btn btn-danger btn-sm">Supprimer</a>
```

```java
@GetMapping("/delete")
    public String delete(@RequestParam Long id,
                         @RequestParam int page,
                         @RequestParam String keyword,
                         RedirectAttributes redirectAttributes) {
        try {
            patientRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Patient supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression du patient");
        }
        return "redirect:/index?page=" + page + "&keyword=" + keyword;
    }
```
---

![image](https://github.com/user-attachments/assets/16aa67b7-1158-4f88-bbec-236525ca5f4b)

![image](https://github.com/user-attachments/assets/468b831b-a9a1-4b6f-a3bf-85896abb57b0)


## Améliorations supplémentaires

### 1. Ajouter des patients

Via formulaire web :

```html
 <div class="col-auto">
      <a th:href="@{/formPatient}" class="btn btn-success">Nouveau Patient</a>
    </div>
```

![image](https://github.com/user-attachments/assets/2a0d6dcf-c0f9-45b2-87f9-3bddd7488661)


### 2. Modifier des patients

```html
 <td>
        <a class="btn btn-warning btn-sm"
           th:href="@{/editPatient(id=${p.id})}">Modifier</a>
      </td>
```
```java
@GetMapping("/editPatient")
    public String editPatient(Model model, @RequestParam Long id) {
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isPresent()) {
            model.addAttribute("patient", patient.get());
            model.addAttribute("title", "Modifier le Patient");
            return "formPatient";
        } else {
            return "redirect:/index?error=PatientNotFound";
        }
    }
```
![image](https://github.com/user-attachments/assets/abe042ec-5c8d-4899-9b54-7f646afe7bfc)




---

## Partie du code explicative

### `PatientController.java`

```java
@GetMapping("/index")
public String patients(Model model,
                       @RequestParam(name="page", defaultValue = "0") int page,
                       @RequestParam(name="size", defaultValue = "5") int size,
                       @RequestParam(name="keyword", defaultValue = "") String keyword) {
    Page<Patient> pagePatients = patientRepository.findByNomContains(keyword, PageRequest.of(page, size));
    model.addAttribute("listPatients", pagePatients.getContent());
    model.addAttribute("pages", new int[pagePatients.getTotalPages()]);
    model.addAttribute("currentPage", page);
    model.addAttribute("keyword", keyword);
    return "patient";
}
```
 une méthode dans un contrôleur Spring MVC, mappée sur l’URL /index, qui gère l'affichage d'une liste paginée de patients avec une recherche par nom.

### `patient.html`

```html
<form method="get" th:action="@{/index}">
    <input type="text" name="keyword" th:value="${keyword}" class="form-control" placeholder="Rechercher par nom">
</form>

<table class="table table-striped table-hover mt-3">
    <thead class="table-dark">
        <tr>
            <th>ID</th><th>Nom</th><th>Date</th><th>Malade</th><th>Score</th><th>Actions</th>
        </tr>
    </thead>
    <tbody>
        <tr th:each="p : ${listPatients}">
            <td th:text="${p.id}"></td>
            <td th:text="${p.nom}"></td>
            <td th:text="${#dates.format(p.dateNaissance, 'yyyy-MM-dd')}"></td>
            <td th:text="${p.malade}"></td>
            <td th:text="${p.score}"></td>
            <td>
                <a th:href="@{/delete(id=${p.id}, keyword=${keyword}, page=${currentPage})}" class="btn btn-danger btn-sm">Supprimer</a>
            </td>
        </tr>
    </tbody>
</table>
```
un formulaire HTML utilisant Thymeleaf (le moteur de templates de Spring Boot) pour ajouter ou modifier un patient.

### `PatientController.java`

```java
@Controller
public class PatientController {
    @Autowired
    private PatientRepository patientRepository;

    @GetMapping("/index")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "5") int size,
                        @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        Page<Patient> pagePatients = patientRepository.findByNomContains(keyword, PageRequest.of(page, size));
        model.addAttribute("listPatients", pagePatients.getContent());
        model.addAttribute("pages", new int[pagePatients.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        return "patients";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam Long id,
                         @RequestParam int page,
                         @RequestParam String keyword,
                         RedirectAttributes redirectAttributes) {
        try {
            patientRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Patient supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression du patient");
        }
        return "redirect:/index?page=" + page + "&keyword=" + keyword;
    }
    @GetMapping("/formPatient")
    public String formPatient(Model model) {
        model.addAttribute("patient", new Patient());
        model.addAttribute("title", "Ajouter un Patient");
        return "formPatient";
    }

    @PostMapping("/save")
    public String savePatient(@Valid @ModelAttribute Patient patient,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("title", patient.getId() == null ? "Ajouter un Patient" : "Modifier le Patient");
            return "formPatient";
        }
        patientRepository.save(patient);
        redirectAttributes.addFlashAttribute("message",
                patient.getId() == null ? "Patient ajouté avec succès" : "Patient modifié avec succès");
        return "redirect:/index";
    }


    @GetMapping("/editPatient")
    public String editPatient(Model model, @RequestParam Long id) {
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isPresent()) {
            model.addAttribute("patient", patient.get());
            model.addAttribute("title", "Modifier le Patient");
            return "formPatient";
        } else {
            return "redirect:/index?error=PatientNotFound";
        }
    }}
```
Le contrôleur Spring Boot (PatientController) de l'application de gestion des patients. Il gère toutes les opérations liées à l’interface utilisateur, comme afficher la liste des patients, ajouter, modifier ou supprimer un patient.

---

