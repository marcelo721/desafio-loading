package br.ufc.loading.requests.infrastructure.adapter.in.web.dto.response;

public record DashboardSummaryResponse(long total, long open, long inProgress, long completed) {
}
