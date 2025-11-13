package br.com.valueprojects.mock_spring.service;

import java.util.List;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Optional;

import br.com.valueprojects.mock_spring.model.Jogo;
import br.com.valueprojects.mock_spring.model.Participante;
import br.com.valueprojects.mock_spring.model.Resultado;
import infra.JogoDao;

public class FinalizadorJogosService {

    private final JogoDao jogoDao;
    private final SMSService smsService;

    public FinalizadorJogosService(JogoDao jogoDao, SMSService smsService) {
        this.jogoDao = jogoDao;
        this.smsService = smsService;
    }

    /**
     * Finaliza todos os jogos da semana anterior, salva na base de dados,
     * e envia SMS para os vencedores.
     *
     * @return número de jogos finalizados
     */
    public int finalizarJogosDaSemanaAnterior() {
        // Obter jogos em andamento
        List<Jogo> jogosEmAndamento = jogoDao.emAndamento();
        int totalFinalizados = 0;

        // Verificar jogos da semana anterior
        for (Jogo jogo : jogosEmAndamento) {
            if (iniciouSemanaAnterior(jogo)) {
                // Finaliza o jogo
                jogo.finaliza();

                // Salva o jogo finalizado na base de dados
                jogoDao.salva(jogo);

                // Identifica o vencedor e envia SMS
                Participante vencedor = determinarVencedor(jogo);
                if (vencedor != null) {
                    smsService.enviarMensagemVitoria(vencedor, jogo.getDescricao());
                }

                totalFinalizados++;
            }
        }

        return totalFinalizados;
    }

    /**
     * Determina o vencedor de um jogo baseado na maior métrica
     */
    private Participante determinarVencedor(Jogo jogo) {
        Optional<Resultado> melhorResultado = jogo.getResultados().stream()
            .max(Comparator.comparing(Resultado::getMetrica));

        return melhorResultado.isPresent() ? melhorResultado.get().getParticipante() : null;
    }

    /**
     * Verifica se o jogo iniciou na semana anterior (7+ dias atrás)
     */
    private boolean iniciouSemanaAnterior(Jogo jogo) {
        Calendar hoje = Calendar.getInstance();
        return diasEntre(jogo.getData(), hoje) >= 7;
    }

    /**
     * Calcula o número de dias entre duas datas
     */
    private int diasEntre(Calendar inicio, Calendar fim) {
        Calendar data = (Calendar) inicio.clone();
        int diasNoIntervalo = 0;

        while (data.before(fim)) {
            data.add(Calendar.DAY_OF_MONTH, 1);
            diasNoIntervalo++;
        }

        return diasNoIntervalo;
    }
}
