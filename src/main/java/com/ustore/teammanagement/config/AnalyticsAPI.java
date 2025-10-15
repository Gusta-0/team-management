package com.ustore.teammanagement.config;

import com.ustore.teammanagement.payload.dto.response.AnalyticsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Tag(
        name = "Analytics",
        description = """
        Endpoints responsáveis por fornecer dados analíticos do sistema:
        - Tarefas ativas, atrasadas e concluídas;
        - Taxa de conclusão no prazo;
        - Distribuição por status e prioridade;
        - Tendências e desempenho por departamento.
        """
)
public interface AnalyticsAPI {

    @Operation(
            summary = "Obter estatísticas gerais de análise",
            description = """
            Retorna dados agregados utilizados nos gráficos e indicadores:
            - Quantidade de tarefas ativas, atrasadas e concluídas no prazo;
            - Quantidade de membros ativos;
            - Distribuição de tarefas por status e prioridade;
            - Dados para gráficos de tendência e desempenho por departamento.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Análises retornadas com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AnalyticsResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/api/analytics")
    ResponseEntity<AnalyticsResponse> getAnalytics();


    @Operation(
            summary = "Obter tendência de tarefas criadas e concluídas",
            description = """
            Retorna dados agregados (diários) de criação e conclusão de tarefas,
            filtrados pelos últimos N dias informados no parâmetro `days`.

            Esses dados alimentam o gráfico de linha exibindo a tendência de conclusão de tarefas.
            """,
            parameters = {
                    @Parameter(
                            name = "days",
                            description = "Quantidade de dias a considerar (padrão: 30)",
                            example = "30"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tendência retornada com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(
                                            example = """
                        [
                          { "date": "2025-09-30", "created": 28, "completed": 0 },
                          { "date": "2025-10-01", "created": 1, "completed": 24 }
                        ]
                        """
                                    ))
                            )
                    )
            }
    )
    @GetMapping("/api/analytics/trend")
    ResponseEntity<List<Map<String, Object>>> getTaskTrend(
            @RequestParam(defaultValue = "30") int days
    );
}
