package com.forumviajeros.backend.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.forumviajeros.backend.model.Country;
import com.forumviajeros.backend.model.TriviaQuestion;
import com.forumviajeros.backend.model.TriviaQuestion.QuestionType;
import com.forumviajeros.backend.repository.CountryRepository;
import com.forumviajeros.backend.repository.TriviaQuestionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Inicializador de datos de países y preguntas de trivia.
 * Solo se ejecuta si no hay países en la base de datos.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
@Profile("!test")
public class CountryDataInitializer implements CommandLineRunner {

    private final CountryRepository countryRepository;
    private final TriviaQuestionRepository triviaQuestionRepository;

    @Override
    public void run(String... args) {
        if (countryRepository.count() == 0) {
            log.info("Inicializando datos de países...");
            initializeCountries();
            log.info("Países inicializados correctamente");
            
            log.info("Generando preguntas de trivia...");
            generateTriviaQuestions();
            log.info("Preguntas de trivia generadas correctamente");
        }
    }

    private void initializeCountries() {
        List<Country> countries = Arrays.asList(
            // EUROPA
            createCountry("ES", "ESP", "España", "Spain", "Reino de España", "Madrid", "Europa", "Europa Occidental",
                "Euro", "EUR", "€", "🇪🇸", 47420000L, 505990.0, 40.4168, -3.7038,
                Arrays.asList("Español"), "+34", "Europe/Madrid"),
            createCountry("FR", "FRA", "Francia", "France", "República Francesa", "París", "Europa", "Europa Occidental",
                "Euro", "EUR", "€", "🇫🇷", 67390000L, 643801.0, 46.2276, 2.2137,
                Arrays.asList("Francés"), "+33", "Europe/Paris"),
            createCountry("DE", "DEU", "Alemania", "Germany", "República Federal de Alemania", "Berlín", "Europa", "Europa Central",
                "Euro", "EUR", "€", "🇩🇪", 83240000L, 357386.0, 51.1657, 10.4515,
                Arrays.asList("Alemán"), "+49", "Europe/Berlin"),
            createCountry("IT", "ITA", "Italia", "Italy", "República Italiana", "Roma", "Europa", "Europa del Sur",
                "Euro", "EUR", "€", "🇮🇹", 60360000L, 301340.0, 41.8719, 12.5674,
                Arrays.asList("Italiano"), "+39", "Europe/Rome"),
            createCountry("GB", "GBR", "Reino Unido", "United Kingdom", "Reino Unido de Gran Bretaña e Irlanda del Norte", "Londres", "Europa", "Europa del Norte",
                "Libra esterlina", "GBP", "£", "🇬🇧", 67886000L, 242495.0, 55.3781, -3.4360,
                Arrays.asList("Inglés"), "+44", "Europe/London"),
            createCountry("PT", "PRT", "Portugal", "Portugal", "República Portuguesa", "Lisboa", "Europa", "Europa del Sur",
                "Euro", "EUR", "€", "🇵🇹", 10196000L, 92212.0, 39.3999, -8.2245,
                Arrays.asList("Portugués"), "+351", "Europe/Lisbon"),
            createCountry("NL", "NLD", "Países Bajos", "Netherlands", "Reino de los Países Bajos", "Ámsterdam", "Europa", "Europa Occidental",
                "Euro", "EUR", "€", "🇳🇱", 17441000L, 41850.0, 52.1326, 5.2913,
                Arrays.asList("Neerlandés"), "+31", "Europe/Amsterdam"),
            createCountry("BE", "BEL", "Bélgica", "Belgium", "Reino de Bélgica", "Bruselas", "Europa", "Europa Occidental",
                "Euro", "EUR", "€", "🇧🇪", 11590000L, 30528.0, 50.5039, 4.4699,
                Arrays.asList("Neerlandés", "Francés", "Alemán"), "+32", "Europe/Brussels"),
            createCountry("CH", "CHE", "Suiza", "Switzerland", "Confederación Suiza", "Berna", "Europa", "Europa Central",
                "Franco suizo", "CHF", "Fr", "🇨🇭", 8654000L, 41284.0, 46.8182, 8.2275,
                Arrays.asList("Alemán", "Francés", "Italiano", "Romanche"), "+41", "Europe/Zurich"),
            createCountry("AT", "AUT", "Austria", "Austria", "República de Austria", "Viena", "Europa", "Europa Central",
                "Euro", "EUR", "€", "🇦🇹", 9006000L, 83871.0, 47.5162, 14.5501,
                Arrays.asList("Alemán"), "+43", "Europe/Vienna"),

            // AMÉRICA DEL NORTE
            createCountry("US", "USA", "Estados Unidos", "United States", "Estados Unidos de América", "Washington D.C.", "América", "América del Norte",
                "Dólar estadounidense", "USD", "$", "🇺🇸", 331900000L, 9833520.0, 37.0902, -95.7129,
                Arrays.asList("Inglés"), "+1", "America/New_York"),
            createCountry("CA", "CAN", "Canadá", "Canada", "Canadá", "Ottawa", "América", "América del Norte",
                "Dólar canadiense", "CAD", "$", "🇨🇦", 38010000L, 9984670.0, 56.1304, -106.3468,
                Arrays.asList("Inglés", "Francés"), "+1", "America/Toronto"),
            createCountry("MX", "MEX", "México", "Mexico", "Estados Unidos Mexicanos", "Ciudad de México", "América", "América del Norte",
                "Peso mexicano", "MXN", "$", "🇲🇽", 128900000L, 1964375.0, 23.6345, -102.5528,
                Arrays.asList("Español"), "+52", "America/Mexico_City"),

            // AMÉRICA DEL SUR
            createCountry("BR", "BRA", "Brasil", "Brazil", "República Federativa del Brasil", "Brasilia", "América", "América del Sur",
                "Real brasileño", "BRL", "R$", "🇧🇷", 212600000L, 8515767.0, -14.2350, -51.9253,
                Arrays.asList("Portugués"), "+55", "America/Sao_Paulo"),
            createCountry("AR", "ARG", "Argentina", "Argentina", "República Argentina", "Buenos Aires", "América", "América del Sur",
                "Peso argentino", "ARS", "$", "🇦🇷", 45380000L, 2780400.0, -38.4161, -63.6167,
                Arrays.asList("Español"), "+54", "America/Buenos_Aires"),
            createCountry("CL", "CHL", "Chile", "Chile", "República de Chile", "Santiago", "América", "América del Sur",
                "Peso chileno", "CLP", "$", "🇨🇱", 19120000L, 756102.0, -35.6751, -71.5430,
                Arrays.asList("Español"), "+56", "America/Santiago"),
            createCountry("CO", "COL", "Colombia", "Colombia", "República de Colombia", "Bogotá", "América", "América del Sur",
                "Peso colombiano", "COP", "$", "🇨🇴", 50880000L, 1141748.0, 4.5709, -74.2973,
                Arrays.asList("Español"), "+57", "America/Bogota"),
            createCountry("PE", "PER", "Perú", "Peru", "República del Perú", "Lima", "América", "América del Sur",
                "Sol peruano", "PEN", "S/", "🇵🇪", 32970000L, 1285216.0, -9.1900, -75.0152,
                Arrays.asList("Español", "Quechua", "Aimara"), "+51", "America/Lima"),

            // ASIA
            createCountry("JP", "JPN", "Japón", "Japan", "Estado de Japón", "Tokio", "Asia", "Asia Oriental",
                "Yen japonés", "JPY", "¥", "🇯🇵", 125800000L, 377975.0, 36.2048, 138.2529,
                Arrays.asList("Japonés"), "+81", "Asia/Tokyo"),
            createCountry("CN", "CHN", "China", "China", "República Popular China", "Pekín", "Asia", "Asia Oriental",
                "Yuan chino", "CNY", "¥", "🇨🇳", 1402000000L, 9596961.0, 35.8617, 104.1954,
                Arrays.asList("Chino mandarín"), "+86", "Asia/Shanghai"),
            createCountry("KR", "KOR", "Corea del Sur", "South Korea", "República de Corea", "Seúl", "Asia", "Asia Oriental",
                "Won surcoreano", "KRW", "₩", "🇰🇷", 51780000L, 100210.0, 35.9078, 127.7669,
                Arrays.asList("Coreano"), "+82", "Asia/Seoul"),
            createCountry("IN", "IND", "India", "India", "República de la India", "Nueva Delhi", "Asia", "Asia del Sur",
                "Rupia india", "INR", "₹", "🇮🇳", 1380000000L, 3287263.0, 20.5937, 78.9629,
                Arrays.asList("Hindi", "Inglés"), "+91", "Asia/Kolkata"),
            createCountry("TH", "THA", "Tailandia", "Thailand", "Reino de Tailandia", "Bangkok", "Asia", "Sudeste Asiático",
                "Baht tailandés", "THB", "฿", "🇹🇭", 69800000L, 513120.0, 15.8700, 100.9925,
                Arrays.asList("Tailandés"), "+66", "Asia/Bangkok"),

            // OCEANÍA
            createCountry("AU", "AUS", "Australia", "Australia", "Mancomunidad de Australia", "Canberra", "Oceanía", "Australasia",
                "Dólar australiano", "AUD", "$", "🇦🇺", 25690000L, 7692024.0, -25.2744, 133.7751,
                Arrays.asList("Inglés"), "+61", "Australia/Sydney"),
            createCountry("NZ", "NZL", "Nueva Zelanda", "New Zealand", "Nueva Zelanda", "Wellington", "Oceanía", "Australasia",
                "Dólar neozelandés", "NZD", "$", "🇳🇿", 5084000L, 268021.0, -40.9006, 174.8860,
                Arrays.asList("Inglés", "Maorí"), "+64", "Pacific/Auckland"),

            // ÁFRICA
            createCountry("ZA", "ZAF", "Sudáfrica", "South Africa", "República de Sudáfrica", "Pretoria", "África", "África del Sur",
                "Rand sudafricano", "ZAR", "R", "🇿🇦", 59310000L, 1221037.0, -30.5595, 22.9375,
                Arrays.asList("Zulú", "Xhosa", "Afrikáans", "Inglés"), "+27", "Africa/Johannesburg"),
            createCountry("EG", "EGY", "Egipto", "Egypt", "República Árabe de Egipto", "El Cairo", "África", "África del Norte",
                "Libra egipcia", "EGP", "£", "🇪🇬", 102300000L, 1002450.0, 26.8206, 30.8025,
                Arrays.asList("Árabe"), "+20", "Africa/Cairo"),
            createCountry("MA", "MAR", "Marruecos", "Morocco", "Reino de Marruecos", "Rabat", "África", "África del Norte",
                "Dírham marroquí", "MAD", "د.م.", "🇲🇦", 36910000L, 446550.0, 31.7917, -7.0926,
                Arrays.asList("Árabe", "Bereber"), "+212", "Africa/Casablanca"),
            createCountry("KE", "KEN", "Kenia", "Kenya", "República de Kenia", "Nairobi", "África", "África Oriental",
                "Chelín keniano", "KES", "KSh", "🇰🇪", 53770000L, 580367.0, -0.0236, 37.9062,
                Arrays.asList("Suajili", "Inglés"), "+254", "Africa/Nairobi")
        );

        countryRepository.saveAll(countries);
    }

    private Country createCountry(String isoCode, String isoCode3, String name, String nameEn, String officialName,
            String capital, String continent, String region, String currencyName, String currencyCode,
            String currencySymbol, String flagEmoji, Long population, Double areaSqKm,
            Double latitude, Double longitude, List<String> languages, String callingCode, String timezone) {
        return Country.builder()
                .isoCode(isoCode)
                .isoCode3(isoCode3)
                .name(name)
                .nameEn(nameEn)
                .officialName(officialName)
                .capital(capital)
                .continent(continent)
                .region(region)
                .currencyName(currencyName)
                .currencyCode(currencyCode)
                .currencySymbol(currencySymbol)
                .flagEmoji(flagEmoji)
                .flagUrl("https://flagcdn.com/w320/" + isoCode.toLowerCase() + ".png")
                .population(population)
                .areaSqKm(areaSqKm)
                .latitude(latitude)
                .longitude(longitude)
                .languages(languages)
                .callingCode(callingCode)
                .timezone(timezone)
                .active(true)
                .build();
    }

    private void generateTriviaQuestions() {
        List<Country> countries = countryRepository.findAll();

        for (Country country : countries) {
            // Pregunta de capital
            TriviaQuestion capitalQ = TriviaQuestion.builder()
                    .questionType(QuestionType.CAPITAL)
                    .country(country)
                    .questionText("¿Cuál es la capital de " + country.getName() + "?")
                    .correctAnswer(country.getCapital())
                    .wrongOptions(getWrongCapitals(country, countries))
                    .difficulty(1)
                    .points(10)
                    .timeLimitSeconds(15)
                    .explanation(country.getCapital() + " es la capital de " + country.getName() + " desde su fundación.")
                    .active(true)
                    .build();
            triviaQuestionRepository.save(capitalQ);

            // Pregunta de bandera
            TriviaQuestion flagQ = TriviaQuestion.builder()
                    .questionType(QuestionType.FLAG)
                    .country(country)
                    .questionText("¿A qué país pertenece esta bandera? " + country.getFlagEmoji())
                    .correctAnswer(country.getName())
                    .wrongOptions(getWrongCountryNames(country, countries))
                    .difficulty(1)
                    .points(10)
                    .timeLimitSeconds(10)
                    .imageUrl(country.getFlagUrl())
                    .active(true)
                    .build();
            triviaQuestionRepository.save(flagQ);

            // Pregunta de moneda
            if (country.getCurrencyName() != null) {
                TriviaQuestion currencyQ = TriviaQuestion.builder()
                        .questionType(QuestionType.CURRENCY)
                        .country(country)
                        .questionText("¿Cuál es la moneda oficial de " + country.getName() + "?")
                        .correctAnswer(country.getCurrencyName())
                        .wrongOptions(getWrongCurrencies(country, countries))
                        .difficulty(2)
                        .points(15)
                        .timeLimitSeconds(15)
                        .explanation("La moneda de " + country.getName() + " es el " + country.getCurrencyName() + " (" + country.getCurrencyCode() + ").")
                        .active(true)
                        .build();
                triviaQuestionRepository.save(currencyQ);
            }

            // Pregunta de continente
            TriviaQuestion continentQ = TriviaQuestion.builder()
                    .questionType(QuestionType.CONTINENT)
                    .country(country)
                    .questionText("¿En qué continente se encuentra " + country.getName() + "?")
                    .correctAnswer(country.getContinent())
                    .wrongOptions(getWrongContinents(country.getContinent()))
                    .difficulty(1)
                    .points(10)
                    .timeLimitSeconds(10)
                    .active(true)
                    .build();
            triviaQuestionRepository.save(continentQ);
        }
    }

    private List<String> getWrongCapitals(Country correct, List<Country> all) {
        return all.stream()
                .filter(c -> !c.getId().equals(correct.getId()))
                .filter(c -> c.getContinent().equals(correct.getContinent()))
                .limit(3)
                .map(Country::getCapital)
                .toList();
    }

    private List<String> getWrongCountryNames(Country correct, List<Country> all) {
        return all.stream()
                .filter(c -> !c.getId().equals(correct.getId()))
                .filter(c -> c.getContinent().equals(correct.getContinent()))
                .limit(3)
                .map(Country::getName)
                .toList();
    }

    private List<String> getWrongCurrencies(Country correct, List<Country> all) {
        return all.stream()
                .filter(c -> !c.getId().equals(correct.getId()))
                .filter(c -> c.getCurrencyName() != null)
                .filter(c -> !c.getCurrencyName().equals(correct.getCurrencyName()))
                .limit(3)
                .map(Country::getCurrencyName)
                .toList();
    }

    private List<String> getWrongContinents(String correct) {
        List<String> continents = Arrays.asList("Europa", "América", "Asia", "África", "Oceanía");
        return continents.stream()
                .filter(c -> !c.equals(correct))
                .limit(3)
                .toList();
    }
}

