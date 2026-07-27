package com.agende.agendeapi.service;

import com.agende.agendeapi.entity.Perfil;
import com.agende.agendeapi.repository.PerfilRepository;
import com.agende.agendeapi.service.interfaces.IPerfilService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PerfilService implements IPerfilService {

    private final PerfilRepository repository;

    public PerfilService(PerfilRepository repository) {
        this.repository = repository;
    }

    public Perfil findByDescricao(String descricao) {
        Optional<Perfil> perfil = Optional.of(repository.findByDescricao(descricao)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado")));

        return perfil.get();
    }

    public Perfil findById(Long id) {
        Optional<Perfil> perfil = Optional.of(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado")));

        return perfil.get();
    }

    @Override
    public List<Perfil> findAll() {
        return repository.findAll();
    }
}
