package org.example.patients.controllers;



import jakarta.validation.Valid;
import org.example.patients.entities.Patient;
import org.example.patients.repositories.PatientRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller

public class PatientController {
    @Autowired
    private PatientRepository patientRepository;

    @GetMapping("/user/index")
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

    @GetMapping("/admin/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
        return "redirect:/user/index?page=" + page + "&keyword=" + keyword;
    }
    @GetMapping("/admin/formPatient")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String formPatient(Model model) {
        model.addAttribute("patient", new Patient());
        model.addAttribute("title", "Ajouter un Patient");
        return "formPatient";
    }

    @PostMapping("/admin/save")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
        return "redirect:/user/index";
    }


    @GetMapping("/admin/editPatient")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editPatient(Model model, @RequestParam Long id) {
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isPresent()) {
            model.addAttribute("patient", patient.get());
            model.addAttribute("title", "Modifier le Patient");
            return "formPatient";
        } else {
            return "redirect:/user/index?error=PatientNotFound";
        }
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/user/index";
    }
}

