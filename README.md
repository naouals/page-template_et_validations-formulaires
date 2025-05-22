
#  Template et Validation de Formulaire

##  Objectifs
1. **Création d'un système de templates unifié**
2. **Mise en place d'une validation robuste des formulaires**
3. **Intégration d'une interface utilisateur moderne et responsive**

##  Fonctionnalités Clés

###  Système de Template
| Fonctionnalité               | Description                                                                 |
|------------------------------|-----------------------------------------------------------------------------|
| **Template parent**          | Structure HTML de base avec navbar, footer et zones d'extension            |
| **Héritage de layout**       | Mécanisme `layout:decorate` pour réutiliser le template parent              |
| **Zones modulaires**         | Sections personnalisables via `layout:fragment`                             |
| **Responsive design**        | Adaptabilité mobile/desktop avec Bootstrap 5                                |
| **Menu dynamique**           | Navigation avec dropdown et recherche intégrée                              |

###  Validation des Formulaires
| Fonctionnalité               | Description                                                                 |
|------------------------------|-----------------------------------------------------------------------------|
| **Validation côté serveur**  | Annotations JSR-380 (`@NotEmpty`, `@NotNull`, etc.)                        |
| **Messages personnalisés**   | Feedback contextuel en français                                             |
| **Validation côté client**   | Attributs HTML5 (required, pattern)                                        |
| **Affichage des erreurs**    | Mise en forme Bootstrap (`is-invalid`, `invalid-feedback`)                 |
| **Gestion des erreurs**      | Rebond sur le formulaire avec conservation des données saisies             |

##  Aperçu Visuel

### Template de Base
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<!-- En-tête commune -->
<body>
  <nav>...</nav> <!-- Barre de navigation -->
  
  <div layout:fragment="content1">
    <!-- Contenu injecté par les pages enfants -->
  </div>

  <footer>...</footer> <!-- Pied de page -->
</body>
</html>
```
![image](https://github.com/user-attachments/assets/27c87988-61cb-405b-833f-d51ebb70b885)


*Navigation avec dropdown et recherche intégrée*
![image](https://github.com/user-attachments/assets/342fa524-eaf4-4856-b677-e076abcacb9d)



# Validation des Formulaires - Détails Techniques

##  Mécanisme de Validation

### 1. **Entité Patient (Validation)**
```java
@Entity
@Data
public class Patient {
    @NotEmpty(message = "Le nom est obligatoire")
    private String nom;
    
    @NotNull(message = "La date est requise")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateNaissance;
    
    @Min(value = 0, message = "Le score ne peut pas être négatif")
    private int score;
}
```
*Les annotations de validation :*
- `@NotEmpty` : Champ requis (String)
- `@NotNull` : Champ requis (autres types)
- `@DateTimeFormat` : Validation du format de date

---

### 2. **Contrôleur (Traitement des erreurs)**
```java
@PostMapping("/save")
public String savePatient(@Valid @ModelAttribute Patient patient, 
                         BindingResult result,
                         Model model) {
    
    if (result.hasErrors()) {
        model.addAttribute("title", patient.getId() == null ? 
                         "Ajouter un Patient" : "Modifier le Patient");
        return "formPatient"; // Reaffiche le formulaire avec erreurs
    }
    // ... sauvegarde si valide
}
```
*Fonctionnalités clés :*
- `@Valid` : Active la validation
- `BindingResult` : Capture les erreurs
- Gestion différenciée création/modification

---

### 3. **Template Thymeleaf (Affichage des erreurs)**
```html
<div class="mb-3">
    <label>Nom :</label>
    <input type="text" class="form-control" 
           th:field="*{nom}" 
           th:classappend="${#fields.hasErrors('nom')} ? 'is-invalid'"/>
    <div class="invalid-feedback" 
         th:if="${#fields.hasErrors('nom')}"
         th:errors="*{nom}"></div>
</div>
```
*Mécanismes :*
- `th:classappend` : Ajoute une classe CSS si erreur
- `th:errors` : Affiche le message d'erreur
- `is-invalid` : Classe Bootstrap pour mise en forme

### 4. Formulaire avec Validation
```html
<div layout:fragment="content1">
  <form th:object="${patient}">
    <!-- Champ avec validation -->
    <input th:field="*{nom}" 
           class="form-control"
           th:classappend="${#fields.hasErrors('nom')} ? 'is-invalid'">
    <div th:if="${#fields.hasErrors('nom')}" 
         th:errors="*{nom}" 
         class="invalid-feedback"></div>
  </form>
</div>
```
---



## Validation côté client (HTML5)
![image](https://github.com/user-attachments/assets/c798e25b-72d5-4cad-8744-4f2abd43a052)

*Les champs requis montrent une indication native*

## Validation côté serveur (Spring)


*Messages d'erreur personnalisés sous les champs*

## Exemple de code erreur
```html
<!-- Affiche ceci quand le nom est vide -->
<div class="invalid-feedback">
    Le nom est obligatoire
</div>
```

---



##  Comment ça marche ?

1. **Mécanisme de Template** :
   - Une seule base HTML (`template1.html`)
   - Extension via `layout:decorate` dans les pages enfants
   - Injection de contenu dans les zones définies

2. **Système de Validation** :
   - Annotations dans l'entité Patient
   - Contrôle dans le contrôleur avec `@Valid` et `BindingResult`
   - Affichage conditionnel des erreurs dans le template

3. **Intégration Bootstrap** :
   - Classes pour le style (`form-control`, `invalid-feedback`)
   - Composants UI (navbar, dropdowns)
   - Responsive design automatique

---

Ce système combine :
- Validation **côté client** (HTML5/Bootstrap)
- Validation **côté serveur** (Spring Validation)
- Affichage **contextuel** des erreurs
- Messages **personnalisables**
