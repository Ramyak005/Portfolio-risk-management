package com.ramya.portfoliorisksystem.seed;

import com.ramya.portfoliorisksystem.model.Stock;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BrandSeeder {

    public List<Stock> generateBrands() {

        List<Stock> brands = new ArrayList<>();

        // Luxury Fashion
        brands.add(new Stock("DIOR", "Dior", "Luxury Fashion", 520));
        brands.add(new Stock("CHNL", "Chanel", "Luxury Fashion", 890));
        brands.add(new Stock("PRDA", "Prada", "Luxury Fashion", 740));
        brands.add(new Stock("GUCC", "Gucci", "Luxury Fashion", 820));
        brands.add(new Stock("LV", "Louis Vuitton", "Luxury Fashion", 980));
        brands.add(new Stock("BURB", "Burberry", "Luxury Fashion", 760));
        brands.add(new Stock("MKOR", "Michael Kors", "Luxury Fashion", 490));

        // Beauty
        brands.add(new Stock("SEPH", "Sephora", "Beauty", 310));
        brands.add(new Stock("NYKA", "Nykaa", "Beauty", 180));
        brands.add(new Stock("RBTY", "Rare Beauty", "Beauty", 420));
        brands.add(new Stock("MAC", "MAC Cosmetics", "Beauty", 250));
        brands.add(new Stock("MBLN", "Maybelline", "Beauty", 150));
        brands.add(new Stock("FENT", "Fenty Beauty", "Beauty", 520));
        brands.add(new Stock("ELF", "e.l.f Beauty", "Beauty", 190));
        brands.add(new Stock("TAR", "Tarte", "Beauty", 270));
        brands.add(new Stock("CLIN", "Clinique", "Beauty", 310));
        brands.add(new Stock("YSL", "YSL Beauty", "Beauty", 650));

        // Fashion Retail
        brands.add(new Stock("ZARA", "Zara", "Fashion Retail", 210));
        brands.add(new Stock("HM", "H&M", "Fashion Retail", 160));
        brands.add(new Stock("UNIQ", "Uniqlo", "Fashion Retail", 230));
        brands.add(new Stock("MNGO", "Mango", "Fashion Retail", 240));
        brands.add(new Stock("VS", "Victoria Secret", "Fashion Retail", 330));
        brands.add(new Stock("AERO", "Aerie", "Fashion Retail", 250));
        brands.add(new Stock("SKMS", "Skims", "Fashion Retail", 470));

        // Sportswear
        brands.add(new Stock("NKE", "Nike", "Sportswear", 450));
        brands.add(new Stock("ADDS", "Adidas", "Sportswear", 430));
        brands.add(new Stock("PUMA", "Puma", "Sportswear", 280));

        // Skincare
        brands.add(new Stock("LUSH", "Lush", "Skincare", 260));
        brands.add(new Stock("LOCC", "LOccitane", "Skincare", 370));
        brands.add(new Stock("BODY", "Body Shop", "Skincare", 290));

        return brands;
    }
}