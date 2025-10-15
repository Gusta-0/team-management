package com.ustore.teammanagement.config;

import com.ustore.teammanagement.payload.dto.response.ActivityResponse;
import com.ustore.teammanagement.payload.dto.response.DashboardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Tag(
        name = "Dashboard",
        description = "Endpoints responsáveis por fornecer estatísticas gerais e atividades recentes do sistema."
)
public interface DashboardAPI {

    @Operation(
            summary = "Obter estatísticas do dashboard",
            description = """
            Retorna informações agregadas para o painel principal:
            - Total de membros cadastrados
            - Tarefas ativas (não concluídas)
            - Revisões pendentes
            - Taxa de conclusão (%) das tarefas
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Estatísticas retornadas com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DashboardResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/api/dashboard")
    ResponseEntity<DashboardResponse> getDashboard();


    @Operation(
            summary = "Obter atividades recentes",
            description = """
            Retorna uma lista com as 5 atividades mais recentes.
            Cada atividade representa uma tarefa criada ou atualizada recentemente.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de atividades recentes retornada com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ActivityResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/api/dashboard/recent-activities")
    ResponseEntity<List<ActivityResponse>> getRecentActivities();
}
