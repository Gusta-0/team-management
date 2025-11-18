package com.ustore.teammanagement.config;

import com.ustore.teammanagement.payload.dto.response.AnalyticsTaskResponse;
import com.ustore.teammanagement.payload.dto.response.OverviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;


@Tag(name = "Analytics", description = "Endpoints para análises de membros, tarefas e projetos")
public interface AnalyticsAPI {

    @Operation(
            summary = "Obter visão geral da aplicação",
            description = "Retorna estatísticas gerais como total de membros, tarefas, projetos, atividades recentes etc.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso",
                            content = @Content(schema = @Schema(implementation = OverviewResponse.class)))
            }
    )
    @GetMapping("/overview")
    ResponseEntity<OverviewResponse> Overview();

    @Operation(
            summary = "Obter análises relacionadas às tarefas",
            description = "Retorna estatísticas por status, prioridade, tendências de criação/conclusão e desempenho por departamento.",
            parameters = {
                    @Parameter(
                            name = "days",
                            description = "Número de dias a serem considerados na tendência de tarefas",
                            example = "30"
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso",
                            content = @Content(schema = @Schema(implementation = AnalyticsTaskResponse.class)))
            }
    )
    @GetMapping("/tasks")
    ResponseEntity<AnalyticsTaskResponse> getAnalyticsTasks(int days);

    @Operation(
            summary = "Análise de desempenho de membros",
            description = "Retorna métricas de produtividade dos membros filtrados por departamento e nome, com suporte a paginação.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso",
                            content = @Content(schema = @Schema(example = """
                            {
                              "members": [
                                {
                                  "id": "b123f4ba-a9a1-4d44-bf89-21e5dcf17fd0",
                                  "name": "Gustavo",
                                  "role": "Backend Developer",
                                  "tasksCompleted": 5,
                                  "tasksAssigned": 10,
                                  "avgCompletionTime": 2.5,
                                  "trend": 1
                                }
                              ],
                              "page": 0,
                              "size": 10,
                              "totalPages": 1,
                              "totalElements": 1
                            }
                            """)))
            }
    )
    @GetMapping("/members")
    ResponseEntity<Map<String, Object>> getMembersAnalysis(
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "Obter progresso dos projetos",
            description = "Retorna uma lista de projetos com informações como progresso, total de tarefas, membros envolvidos e status.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de projetos retornada com sucesso",
                            content = @Content(schema = @Schema(example = """
                            {
                              "projects": [
                                {
                                  "id": "41ac5c2c-083b-4f31-9eef-7d8b347aab31",
                                  "name": "Desenvolvimento Web",
                                  "description": "Projeto Desenvolvimento Web",
                                  "progress": 75.0,
                                  "totalTasks": 20,
                                  "completedTasks": 15,
                                  "teamMembers": 4,
                                  "dueDate": "2025-03-10",
                                  "status": "IN_PROGRESS",
                                  "priority": "HIGH"
                                }
                              ]
                            }
                            """)))
            }
    )
    @GetMapping("/projects")
    public ResponseEntity<Map<String, Object>> getProjectsAnalysis() ;
}
