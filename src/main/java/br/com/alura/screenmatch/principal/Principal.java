package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private List<DadosSerie> dadosSeries = new ArrayList<>();
    private List<Categoria> generos = new ArrayList<>();

    private SerieRepository serieRepository;

    private List<Serie> series = new ArrayList<>();

    private Optional<Serie> serieBusca;

    public Principal(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }

    public void exibeMenu() {
        var opcao = -1;
        while ( opcao != 0 ) {
        var menu = """
                --------------------------------------------
                1 - Buscar séries
                2 - Buscar episódio
                3 - Listar séris buscadas
                4 - Buscar Série por titulo
                5 - Buscar Série por ator
                6 - Top 5 Series
                7 - Buscar séries por categoria
                8 - Filtrar séries
                9 - Buscar episodios por trecho
                10 - Buscar top 5 episodios
                11 - Buscar episódios a partir de uma data
                
                0 - Sair
                --------------------------------------------
                """;

        System.out.println(menu);

        opcao = leitura.nextInt();
        leitura.nextLine();

        switch (opcao) {
            case 1:
                buscarSerieWeb();
                break;
            case 2:
                buscarEpisodioPorSerie();
                break;
            case 3:
                listarSerieBuscadas();
                break;
            case 4:
                buscarSeriePorTitulo();
                break;
            case 5:
                buscarSeriePorAtor();
                break;
            case 6:
                buscarTopSeries();
                break;
            case 7:
                buscarSeriePorCategoria();
                break;
            case 8:
                buscarSeriePorMaxTotalTemporadas();
                break;
            case 9:
                buscarEpisodioPorTrecho();
                break;
            case 10:
                topEpisodiosPorSerie();
                break;
            case 11:
                buscarEpisodiosPorData();
                break;
            case 0:
                System.out.println("Saindo...");
                break;
            default:
                System.out.println("Opção inválida");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        //dadosSeries.add(dados)
        serieRepository.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSerieBuscadas();
        System.out.println("Selecione a série para busca");
        var nomeSerie = leitura.nextLine();

//        Optional<Serie> serie = series.stream()
//                .filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
//                .findFirst();

        Optional<Serie> serie = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            serieRepository.save(serieEncontrada);

        } else {
            System.out.println("Serie não encontrada");
        }
    }

    private void listarSerieBuscadas() {
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("Séries:");
        series = serieRepository.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

    }

    private void buscarSeriePorTitulo() {
        System.out.println("Selecione a série para busca");
        var nomeSerie = leitura.nextLine();
        serieBusca = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.println("Dados da série: " + serieBusca.get());
        } else {
            System.out.println("Série não encontrada");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Qual o nome do ator para busca?: ");
        var nomeAtor = leitura.nextLine();
        System.out.println("Avaliações a partir de qual valor?");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesEncontradas = serieRepository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("Séries em que " + nomeAtor + " trabalhou");
        seriesEncontradas.forEach(s ->
                System.out.println("Série: " + s.getTitulo() + ", Avaliação: " + s.getAvaliacao()));
    }

    private void buscarTopSeries(){
        List<Serie> topSerie = serieRepository.findTop5ByOrderByAvaliacaoDesc();
        topSerie.forEach(ts ->
                System.out.println("Série: " + ts.getTitulo() + ", Avaliacao: " + ts.getAvaliacao()));
    }

    private void buscarSeriePorCategoria(){
        System.out.println("Qual série você deseja buscar?");
        var categoriaBuscada = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(categoriaBuscada);
        List<Serie> seriesPorCategoria = serieRepository.findByGenero(categoria);
        System.out.println("Séries da categoria " + categoriaBuscada);
        seriesPorCategoria.forEach(System.out::println);

    }

    private void buscarSeriePorMaxTotalTemporadas() {
        System.out.println("Filtrar séries até quantas temporadas? ");
        var totalTemporadas = leitura.nextInt();
        System.out.println("Com avaliação a partir de que valor? ");
        var avaliacao = leitura.nextDouble();
        List<Serie> serieMaxTemporada = serieRepository.seriePorTemporadaEAvaliacao(totalTemporadas, avaliacao);
        System.out.println("***** Séries Filtradas *****");
        serieMaxTemporada.forEach(s ->
                System.out.println("Série: " + s.getTitulo() + ", TotalTemporadas: " + s.getTotalTemporadas() + ", Avaliacao: " + s.getAvaliacao()));
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Qual o nome do ep para busca?");
        var trechoEpisodio = leitura.nextLine();
        List<Episodio> episodiosEncontrados = serieRepository.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Série: %s Temporada %s - Episódio %s - %s \n", e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()));
    }

    private void topEpisodiosPorSerie() {
        buscarSeriePorTitulo();
        System.out.println("Qual série você deseja ver o TOP 5 de episodios");

        if (serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = serieRepository.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e ->
                    System.out.printf("Serie: %s Temporada %s - Episódio %s Avaliação: %s - %s\n",
                    e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
        }
    }

    private void buscarEpisodiosPorData(){
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            System.out.println("Digite o ano limite de lançamento");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = serieRepository.episodioPorSerieEAno(serie, anoLancamento);
            episodiosAno.forEach(System.out::println);
        }
    }

}