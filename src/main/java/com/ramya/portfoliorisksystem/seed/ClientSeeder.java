package com.ramya.portfoliorisksystem.seed;

import com.ramya.portfoliorisksystem.model.Client;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class ClientSeeder {

    private final Random random = new Random();

    private final String[] firstNames = {
            "Emma","Olivia","Sophia","Ava","Isabella",
            "Mia","Charlotte","Amelia","Harper","Evelyn",
            "Luna","Scarlett","Aria","Ella","Grace",
            "Chloe","Nora","Zoe","Lily","Hannah"
    };

    private final String[] lastNames = {
            "Johnson","Smith","Brown","Garcia","Taylor",
            "Wilson","Lee","Martin","Hall","Walker",
            "Allen","Young","King","Scott","Green"
    };

    // Must match BrandSeeder symbols
    private final String[] brands = {
            "DIOR","CHNL","PRDA","GUCC","LV","BURB","MKOR",
            "SEPH","NYKA","RBTY","MAC","MBLN","FENT","SKMS",
            "NKE","ADID","ZARA","HM","PUMA","UNIQ",
            "YSL","VERS","BALN","TFFY","ARMN","COACH",
            "MANGO","ULTA","ELF","AESP"
    };

    public List<Client> generateClients() {

        List<Client> clients = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {

            String name =
                    firstNames[random.nextInt(firstNames.length)]
                            + " " +
                            lastNames[random.nextInt(lastNames.length)];

            Map<String, Integer> holdings = generateHoldings();

            double portfolioValue =
                    50000 + random.nextDouble() * 450000;

            double diversification =
                    55 + random.nextDouble() * 40;

            double topRisk =
                    8 + random.nextDouble() * 25;

            Client client = new Client(
                    i,
                    name,
                    portfolioValue,
                    holdings,
                    diversification,
                    topRisk
            );

            clients.add(client);
        }

        return clients;
    }

    private Map<String, Integer> generateHoldings() {

        Map<String, Integer> holdings = new HashMap<>();

        int count = 4 + random.nextInt(5); // 4–8 brands

        for (int i = 0; i < count; i++) {

            String symbol =
                    brands[random.nextInt(brands.length)];

            int shares =
                    10 + random.nextInt(150);

            holdings.put(symbol, shares);
        }

        return holdings;
    }
}