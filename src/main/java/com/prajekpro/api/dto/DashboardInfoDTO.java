package com.prajekpro.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DashboardInfoDTO {

    private SummaryForDashboard overallSummary;
    private SummaryForDashboard todaySummary;

    private List<PrajekProWalletDTO> prajekProWallet;


    public DashboardInfoDTO(SummaryForDashboard todaySummary, SummaryForDashboard overallSummaryForDashboard, List<PrajekProWalletDTO> prajekProWallet) {
        this.overallSummary = overallSummaryForDashboard;
        this.todaySummary = todaySummary;
        this.prajekProWallet = prajekProWallet;
    }
}
