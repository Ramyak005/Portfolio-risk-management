package com.ramya.portfoliorisksystem.seed;

import com.ramya.portfoliorisksystem.model.Client;
import com.ramya.portfoliorisksystem.model.Portfolio;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PortfolioSeeder {

    public List<Portfolio> generatePortfolios(List<Client> clients) {

        List<Portfolio> portfolios = new ArrayList<>();

        for (Client client : clients) {

            Portfolio portfolio = new Portfolio(
                    client.getName(),
                    client.getPortfolioValue(),
                    client.getHoldings()
            );

            portfolios.add(portfolio);
        }

        return portfolios;
    }
}