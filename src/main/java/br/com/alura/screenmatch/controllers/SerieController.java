package br.com.alura.screenmatch.controllers;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;                          // Baixo acoplamento, alta coesão

import java.util.List;

@RestController
@RequestMapping("/series")

public class SerieController {

    @Autowired
    SerieService serieService;

    @GetMapping
    public List<SerieDTO> obterSeries() {
        return serieService.obterTodasAsSeries();
    }

    @GetMapping("/top5")
    public List<SerieDTO> obterSerieTop5() {
        return serieService.obterTop5Series();
    }

    @GetMapping("/lancamentos")
    public List<SerieDTO> obterSerieLancamentos() {
        return serieService.obterLancamentos();
    }

    @GetMapping("/{id}")
    public SerieDTO obterSeriePorId(@PathVariable Long id) {
        return serieService.obterPorId(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO> obterTodasTemporadas(@PathVariable Long id) {
        return serieService.obterTodasTemporadas(id);
    }

    @GetMapping("/{id}/temporadas/{numero}")
    public List<EpisodioDTO> obterTemporadasPorNumero(@PathVariable Long id, @PathVariable Integer numero) {
        return serieService.obterTemporadaPorNumero(id, numero);
    }

    @GetMapping("/categoria/{nomeGenero}")
    public List<SerieDTO> obterSeriePorGenero(@PathVariable String nomeGenero) {
        return serieService.obterSeriePorGenero(nomeGenero);
    }

}
