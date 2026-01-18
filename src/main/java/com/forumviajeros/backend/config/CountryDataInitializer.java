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
 * Inicializador de datos de paises y preguntas de trivia.
 * Incluye los 195 paises reconocidos por la ONU.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
@Profile("!test")
public class CountryDataInitializer implements CommandLineRunner {

    private final CountryRepository countryRepository;
    private final TriviaQuestionRepository triviaQuestionRepository;
    private final com.forumviajeros.backend.repository.TriviaAnswerRepository triviaAnswerRepository;
    private final com.forumviajeros.backend.repository.TriviaGameRepository triviaGameRepository;
    private final com.forumviajeros.backend.repository.TriviaScoreRepository triviaScoreRepository;
    private final com.forumviajeros.backend.repository.VisitedPlaceRepository visitedPlaceRepository;

    private static final int EXPECTED_COUNTRIES = 195;

    @Override
    public void run(String... args) {
        long countryCount = countryRepository.count();
        long triviaCount = triviaQuestionRepository.count();

        log.info("===== DATA INITIALIZATION STATUS =====");
        log.info("Current countries in database: {}", countryCount);
        log.info("Current trivia questions in database: {}", triviaCount);

        boolean needsReload = countryCount == 0 || countryCount < EXPECTED_COUNTRIES;

        if (needsReload) {
            if (countryCount > 0) {
                log.warn("INCOMPLETE DATA DETECTED! Cleaning and reloading...");
                log.warn("Expected {} countries but found {}", EXPECTED_COUNTRIES, countryCount);

                log.info("Deleting incomplete data...");
                triviaAnswerRepository.deleteAll();
                triviaScoreRepository.deleteAll();
                triviaGameRepository.deleteAll();
                triviaQuestionRepository.deleteAll();
                visitedPlaceRepository.deleteAll();
                countryRepository.deleteAll();
                log.info("Data cleaned successfully");
            }

            log.info("Inicializando datos de {} paises...", EXPECTED_COUNTRIES);
            initializeCountries();
            log.info("Paises inicializados correctamente");

            log.info("Generando preguntas de trivia...");
            generateTriviaQuestions();
            log.info("Preguntas de trivia generadas correctamente");

            long finalCountries = countryRepository.count();
            long finalTrivia = triviaQuestionRepository.count();
            log.info("Final countries count: {}", finalCountries);
            log.info("Final trivia questions count: {}", finalTrivia);

            if (finalCountries >= EXPECTED_COUNTRIES) {
                log.info("DATA INITIALIZATION SUCCESSFUL!");
            } else {
                log.error("DATA INITIALIZATION INCOMPLETE!");
            }
        } else {
            log.info("Data already complete. Skipping initialization.");
        }
        log.info("======================================");
    }

    private void initializeCountries() {
        List<Country> countries = Arrays.asList(
            // ===================== EUROPA (44 paises) =====================
            createCountry("AL", "ALB", "Albania", "Albania", "Republica de Albania", "Tirana", "Europa", "Europa del Sur", "Lek albanes", "ALL", "L", 2877000L, 28748.0, 41.1533, 20.1683, Arrays.asList("Albanes"), "+355", "Europe/Tirane"),
            createCountry("AD", "AND", "Andorra", "Andorra", "Principado de Andorra", "Andorra la Vella", "Europa", "Europa del Sur", "Euro", "EUR", "E", 77265L, 468.0, 42.5063, 1.5218, Arrays.asList("Catalan"), "+376", "Europe/Andorra"),
            createCountry("AT", "AUT", "Austria", "Austria", "Republica de Austria", "Viena", "Europa", "Europa Central", "Euro", "EUR", "E", 9006000L, 83871.0, 47.5162, 14.5501, Arrays.asList("Aleman"), "+43", "Europe/Vienna"),
            createCountry("BY", "BLR", "Bielorrusia", "Belarus", "Republica de Bielorrusia", "Minsk", "Europa", "Europa del Este", "Rublo bielorruso", "BYN", "Br", 9449000L, 207600.0, 53.7098, 27.9534, Arrays.asList("Bielorruso", "Ruso"), "+375", "Europe/Minsk"),
            createCountry("BE", "BEL", "Belgica", "Belgium", "Reino de Belgica", "Bruselas", "Europa", "Europa Occidental", "Euro", "EUR", "E", 11590000L, 30528.0, 50.5039, 4.4699, Arrays.asList("Neerlandes", "Frances", "Aleman"), "+32", "Europe/Brussels"),
            createCountry("BA", "BIH", "Bosnia y Herzegovina", "Bosnia and Herzegovina", "Bosnia y Herzegovina", "Sarajevo", "Europa", "Europa del Sur", "Marco convertible", "BAM", "KM", 3280000L, 51197.0, 43.9159, 17.6791, Arrays.asList("Bosnio", "Croata", "Serbio"), "+387", "Europe/Sarajevo"),
            createCountry("BG", "BGR", "Bulgaria", "Bulgaria", "Republica de Bulgaria", "Sofia", "Europa", "Europa del Este", "Lev bulgaro", "BGN", "лв", 6948000L, 110879.0, 42.7339, 25.4858, Arrays.asList("Bulgaro"), "+359", "Europe/Sofia"),
            createCountry("HR", "HRV", "Croacia", "Croatia", "Republica de Croacia", "Zagreb", "Europa", "Europa del Sur", "Euro", "EUR", "E", 4105000L, 56594.0, 45.1, 15.2, Arrays.asList("Croata"), "+385", "Europe/Zagreb"),
            createCountry("CY", "CYP", "Chipre", "Cyprus", "Republica de Chipre", "Nicosia", "Europa", "Europa del Sur", "Euro", "EUR", "E", 1207000L, 9251.0, 35.1264, 33.4299, Arrays.asList("Griego", "Turco"), "+357", "Europe/Nicosia"),
            createCountry("CZ", "CZE", "Chequia", "Czech Republic", "Republica Checa", "Praga", "Europa", "Europa Central", "Corona checa", "CZK", "Kc", 10708000L, 78867.0, 49.8175, 15.4730, Arrays.asList("Checo"), "+420", "Europe/Prague"),
            createCountry("DK", "DNK", "Dinamarca", "Denmark", "Reino de Dinamarca", "Copenhague", "Europa", "Europa del Norte", "Corona danesa", "DKK", "kr", 5831000L, 43094.0, 56.2639, 9.5018, Arrays.asList("Danes"), "+45", "Europe/Copenhagen"),
            createCountry("EE", "EST", "Estonia", "Estonia", "Republica de Estonia", "Tallin", "Europa", "Europa del Norte", "Euro", "EUR", "E", 1331000L, 45228.0, 58.5953, 25.0136, Arrays.asList("Estonio"), "+372", "Europe/Tallinn"),
            createCountry("FI", "FIN", "Finlandia", "Finland", "Republica de Finlandia", "Helsinki", "Europa", "Europa del Norte", "Euro", "EUR", "E", 5541000L, 338424.0, 61.9241, 25.7482, Arrays.asList("Fines", "Sueco"), "+358", "Europe/Helsinki"),
            createCountry("FR", "FRA", "Francia", "France", "Republica Francesa", "Paris", "Europa", "Europa Occidental", "Euro", "EUR", "E", 67390000L, 643801.0, 46.2276, 2.2137, Arrays.asList("Frances"), "+33", "Europe/Paris"),
            createCountry("DE", "DEU", "Alemania", "Germany", "Republica Federal de Alemania", "Berlin", "Europa", "Europa Central", "Euro", "EUR", "E", 83240000L, 357386.0, 51.1657, 10.4515, Arrays.asList("Aleman"), "+49", "Europe/Berlin"),
            createCountry("GR", "GRC", "Grecia", "Greece", "Republica Helenica", "Atenas", "Europa", "Europa del Sur", "Euro", "EUR", "E", 10423000L, 131957.0, 39.0742, 21.8243, Arrays.asList("Griego"), "+30", "Europe/Athens"),
            createCountry("HU", "HUN", "Hungria", "Hungary", "Hungria", "Budapest", "Europa", "Europa Central", "Forint hungaro", "HUF", "Ft", 9660000L, 93028.0, 47.1625, 19.5033, Arrays.asList("Hungaro"), "+36", "Europe/Budapest"),
            createCountry("IS", "ISL", "Islandia", "Iceland", "Republica de Islandia", "Reikiavik", "Europa", "Europa del Norte", "Corona islandesa", "ISK", "kr", 372000L, 103000.0, 64.9631, -19.0208, Arrays.asList("Islandes"), "+354", "Atlantic/Reykjavik"),
            createCountry("IE", "IRL", "Irlanda", "Ireland", "Republica de Irlanda", "Dublin", "Europa", "Europa del Norte", "Euro", "EUR", "E", 5011000L, 70273.0, 53.1424, -7.6921, Arrays.asList("Irlandes", "Ingles"), "+353", "Europe/Dublin"),
            createCountry("IT", "ITA", "Italia", "Italy", "Republica Italiana", "Roma", "Europa", "Europa del Sur", "Euro", "EUR", "E", 60360000L, 301340.0, 41.8719, 12.5674, Arrays.asList("Italiano"), "+39", "Europe/Rome"),
            createCountry("XK", "XKX", "Kosovo", "Kosovo", "Republica de Kosovo", "Pristina", "Europa", "Europa del Sur", "Euro", "EUR", "E", 1800000L, 10887.0, 42.6026, 20.9030, Arrays.asList("Albanes", "Serbio"), "+383", "Europe/Belgrade"),
            createCountry("LV", "LVA", "Letonia", "Latvia", "Republica de Letonia", "Riga", "Europa", "Europa del Norte", "Euro", "EUR", "E", 1886000L, 64589.0, 56.8796, 24.6032, Arrays.asList("Leton"), "+371", "Europe/Riga"),
            createCountry("LI", "LIE", "Liechtenstein", "Liechtenstein", "Principado de Liechtenstein", "Vaduz", "Europa", "Europa Central", "Franco suizo", "CHF", "Fr", 38128L, 160.0, 47.1660, 9.5554, Arrays.asList("Aleman"), "+423", "Europe/Vaduz"),
            createCountry("LT", "LTU", "Lituania", "Lithuania", "Republica de Lituania", "Vilna", "Europa", "Europa del Norte", "Euro", "EUR", "E", 2795000L, 65300.0, 55.1694, 23.8813, Arrays.asList("Lituano"), "+370", "Europe/Vilnius"),
            createCountry("LU", "LUX", "Luxemburgo", "Luxembourg", "Gran Ducado de Luxemburgo", "Luxemburgo", "Europa", "Europa Occidental", "Euro", "EUR", "E", 634814L, 2586.0, 49.8153, 6.1296, Arrays.asList("Luxemburgues", "Frances", "Aleman"), "+352", "Europe/Luxembourg"),
            createCountry("MT", "MLT", "Malta", "Malta", "Republica de Malta", "La Valeta", "Europa", "Europa del Sur", "Euro", "EUR", "E", 514564L, 316.0, 35.9375, 14.3754, Arrays.asList("Maltes", "Ingles"), "+356", "Europe/Malta"),
            createCountry("MD", "MDA", "Moldavia", "Moldova", "Republica de Moldavia", "Chisinau", "Europa", "Europa del Este", "Leu moldavo", "MDL", "L", 2617000L, 33846.0, 47.4116, 28.3699, Arrays.asList("Rumano"), "+373", "Europe/Chisinau"),
            createCountry("MC", "MCO", "Monaco", "Monaco", "Principado de Monaco", "Monaco", "Europa", "Europa Occidental", "Euro", "EUR", "E", 39242L, 2.02, 43.7384, 7.4246, Arrays.asList("Frances"), "+377", "Europe/Monaco"),
            createCountry("ME", "MNE", "Montenegro", "Montenegro", "Montenegro", "Podgorica", "Europa", "Europa del Sur", "Euro", "EUR", "E", 621718L, 13812.0, 42.7087, 19.3744, Arrays.asList("Montenegrino"), "+382", "Europe/Podgorica"),
            createCountry("NL", "NLD", "Paises Bajos", "Netherlands", "Reino de los Paises Bajos", "Amsterdam", "Europa", "Europa Occidental", "Euro", "EUR", "E", 17441000L, 41850.0, 52.1326, 5.2913, Arrays.asList("Neerlandes"), "+31", "Europe/Amsterdam"),
            createCountry("MK", "MKD", "Macedonia del Norte", "North Macedonia", "Republica de Macedonia del Norte", "Skopie", "Europa", "Europa del Sur", "Denar macedonio", "MKD", "den", 2083000L, 25713.0, 41.5124, 21.7453, Arrays.asList("Macedonio"), "+389", "Europe/Skopje"),
            createCountry("NO", "NOR", "Noruega", "Norway", "Reino de Noruega", "Oslo", "Europa", "Europa del Norte", "Corona noruega", "NOK", "kr", 5421000L, 323802.0, 60.4720, 8.4689, Arrays.asList("Noruego"), "+47", "Europe/Oslo"),
            createCountry("PL", "POL", "Polonia", "Poland", "Republica de Polonia", "Varsovia", "Europa", "Europa Central", "Esloti polaco", "PLN", "zl", 37846000L, 312685.0, 51.9194, 19.1451, Arrays.asList("Polaco"), "+48", "Europe/Warsaw"),
            createCountry("PT", "PRT", "Portugal", "Portugal", "Republica Portuguesa", "Lisboa", "Europa", "Europa del Sur", "Euro", "EUR", "E", 10196000L, 92212.0, 39.3999, -8.2245, Arrays.asList("Portugues"), "+351", "Europe/Lisbon"),
            createCountry("RO", "ROU", "Rumania", "Romania", "Rumania", "Bucarest", "Europa", "Europa del Este", "Leu rumano", "RON", "lei", 19237000L, 238391.0, 45.9432, 24.9668, Arrays.asList("Rumano"), "+40", "Europe/Bucharest"),
            createCountry("RU", "RUS", "Rusia", "Russia", "Federacion de Rusia", "Moscu", "Europa", "Europa del Este", "Rublo ruso", "RUB", "P", 144100000L, 17098242.0, 61.5240, 105.3188, Arrays.asList("Ruso"), "+7", "Europe/Moscow"),
            createCountry("SM", "SMR", "San Marino", "San Marino", "Republica de San Marino", "San Marino", "Europa", "Europa del Sur", "Euro", "EUR", "E", 33931L, 61.0, 43.9424, 12.4578, Arrays.asList("Italiano"), "+378", "Europe/San_Marino"),
            createCountry("RS", "SRB", "Serbia", "Serbia", "Republica de Serbia", "Belgrado", "Europa", "Europa del Sur", "Dinar serbio", "RSD", "din", 6908000L, 88361.0, 44.0165, 21.0059, Arrays.asList("Serbio"), "+381", "Europe/Belgrade"),
            createCountry("SK", "SVK", "Eslovaquia", "Slovakia", "Republica Eslovaca", "Bratislava", "Europa", "Europa Central", "Euro", "EUR", "E", 5460000L, 49035.0, 48.6690, 19.6990, Arrays.asList("Eslovaco"), "+421", "Europe/Bratislava"),
            createCountry("SI", "SVN", "Eslovenia", "Slovenia", "Republica de Eslovenia", "Liubliana", "Europa", "Europa Central", "Euro", "EUR", "E", 2078000L, 20273.0, 46.1512, 14.9955, Arrays.asList("Esloveno"), "+386", "Europe/Ljubljana"),
            createCountry("ES", "ESP", "Espana", "Spain", "Reino de Espana", "Madrid", "Europa", "Europa del Sur", "Euro", "EUR", "E", 47420000L, 505990.0, 40.4168, -3.7038, Arrays.asList("Espanol"), "+34", "Europe/Madrid"),
            createCountry("SE", "SWE", "Suecia", "Sweden", "Reino de Suecia", "Estocolmo", "Europa", "Europa del Norte", "Corona sueca", "SEK", "kr", 10423000L, 450295.0, 60.1282, 18.6435, Arrays.asList("Sueco"), "+46", "Europe/Stockholm"),
            createCountry("CH", "CHE", "Suiza", "Switzerland", "Confederacion Suiza", "Berna", "Europa", "Europa Central", "Franco suizo", "CHF", "Fr", 8654000L, 41284.0, 46.8182, 8.2275, Arrays.asList("Aleman", "Frances", "Italiano", "Romanche"), "+41", "Europe/Zurich"),
            createCountry("UA", "UKR", "Ucrania", "Ukraine", "Ucrania", "Kiev", "Europa", "Europa del Este", "Grivna ucraniana", "UAH", "?", 41170000L, 603550.0, 48.3794, 31.1656, Arrays.asList("Ucraniano"), "+380", "Europe/Kiev"),
            createCountry("GB", "GBR", "Reino Unido", "United Kingdom", "Reino Unido de Gran Bretana e Irlanda del Norte", "Londres", "Europa", "Europa del Norte", "Libra esterlina", "GBP", "lb", 67886000L, 242495.0, 55.3781, -3.4360, Arrays.asList("Ingles"), "+44", "Europe/London"),
            createCountry("VA", "VAT", "Ciudad del Vaticano", "Vatican City", "Estado de la Ciudad del Vaticano", "Ciudad del Vaticano", "Europa", "Europa del Sur", "Euro", "EUR", "E", 825L, 0.44, 41.9029, 12.4534, Arrays.asList("Italiano", "Latin"), "+39", "Europe/Vatican"),

            // ===================== AMERICA DEL NORTE Y CENTRAL (23 paises) =====================
            createCountry("AG", "ATG", "Antigua y Barbuda", "Antigua and Barbuda", "Antigua y Barbuda", "Saint John's", "America", "Caribe", "Dolar del Caribe Oriental", "XCD", "$", 97929L, 442.6, 17.0608, -61.7964, Arrays.asList("Ingles"), "+1", "America/Antigua"),
            createCountry("BS", "BHS", "Bahamas", "Bahamas", "Mancomunidad de las Bahamas", "Nassau", "America", "Caribe", "Dolar bahames", "BSD", "$", 393244L, 13878.0, 25.0343, -77.3963, Arrays.asList("Ingles"), "+1", "America/Nassau"),
            createCountry("BB", "BRB", "Barbados", "Barbados", "Barbados", "Bridgetown", "America", "Caribe", "Dolar de Barbados", "BBD", "$", 287375L, 430.0, 13.1939, -59.5432, Arrays.asList("Ingles"), "+1", "America/Barbados"),
            createCountry("BZ", "BLZ", "Belice", "Belize", "Belice", "Belmopan", "America", "America Central", "Dolar beliceno", "BZD", "$", 397628L, 22966.0, 17.1899, -88.4976, Arrays.asList("Ingles", "Espanol"), "+501", "America/Belize"),
            createCountry("CA", "CAN", "Canada", "Canada", "Canada", "Ottawa", "America", "America del Norte", "Dolar canadiense", "CAD", "$", 38010000L, 9984670.0, 56.1304, -106.3468, Arrays.asList("Ingles", "Frances"), "+1", "America/Toronto"),
            createCountry("CR", "CRI", "Costa Rica", "Costa Rica", "Republica de Costa Rica", "San Jose", "America", "America Central", "Colon costarricense", "CRC", "C", 5094000L, 51100.0, 9.7489, -83.7534, Arrays.asList("Espanol"), "+506", "America/Costa_Rica"),
            createCountry("CU", "CUB", "Cuba", "Cuba", "Republica de Cuba", "La Habana", "America", "Caribe", "Peso cubano", "CUP", "$", 11326000L, 109884.0, 21.5218, -77.7812, Arrays.asList("Espanol"), "+53", "America/Havana"),
            createCountry("DM", "DMA", "Dominica", "Dominica", "Mancomunidad de Dominica", "Roseau", "America", "Caribe", "Dolar del Caribe Oriental", "XCD", "$", 71986L, 751.0, 15.4150, -61.3710, Arrays.asList("Ingles"), "+1", "America/Dominica"),
            createCountry("DO", "DOM", "Republica Dominicana", "Dominican Republic", "Republica Dominicana", "Santo Domingo", "America", "Caribe", "Peso dominicano", "DOP", "$", 10847000L, 48671.0, 18.7357, -70.1627, Arrays.asList("Espanol"), "+1", "America/Santo_Domingo"),
            createCountry("SV", "SLV", "El Salvador", "El Salvador", "Republica de El Salvador", "San Salvador", "America", "America Central", "Dolar estadounidense", "USD", "$", 6486000L, 21041.0, 13.7942, -88.8965, Arrays.asList("Espanol"), "+503", "America/El_Salvador"),
            createCountry("GD", "GRD", "Granada", "Grenada", "Granada", "Saint George's", "America", "Caribe", "Dolar del Caribe Oriental", "XCD", "$", 112523L, 344.0, 12.2628, -61.6043, Arrays.asList("Ingles"), "+1", "America/Grenada"),
            createCountry("GT", "GTM", "Guatemala", "Guatemala", "Republica de Guatemala", "Ciudad de Guatemala", "America", "America Central", "Quetzal guatemalteco", "GTQ", "Q", 17915000L, 108889.0, 15.7835, -90.2308, Arrays.asList("Espanol"), "+502", "America/Guatemala"),
            createCountry("HT", "HTI", "Haiti", "Haiti", "Republica de Haiti", "Puerto Principe", "America", "Caribe", "Gourde haitiano", "HTG", "G", 11402000L, 27750.0, 18.9712, -72.2852, Arrays.asList("Frances", "Criollo haitiano"), "+509", "America/Port-au-Prince"),
            createCountry("HN", "HND", "Honduras", "Honduras", "Republica de Honduras", "Tegucigalpa", "America", "America Central", "Lempira hondureno", "HNL", "L", 9905000L, 112492.0, 15.2000, -86.2419, Arrays.asList("Espanol"), "+504", "America/Tegucigalpa"),
            createCountry("JM", "JAM", "Jamaica", "Jamaica", "Jamaica", "Kingston", "America", "Caribe", "Dolar jamaiquino", "JMD", "$", 2961000L, 10991.0, 18.1096, -77.2975, Arrays.asList("Ingles"), "+1", "America/Jamaica"),
            createCountry("MX", "MEX", "Mexico", "Mexico", "Estados Unidos Mexicanos", "Ciudad de Mexico", "America", "America del Norte", "Peso mexicano", "MXN", "$", 128900000L, 1964375.0, 23.6345, -102.5528, Arrays.asList("Espanol"), "+52", "America/Mexico_City"),
            createCountry("NI", "NIC", "Nicaragua", "Nicaragua", "Republica de Nicaragua", "Managua", "America", "America Central", "Cordoba nicaraguense", "NIO", "C$", 6624000L, 130373.0, 12.8654, -85.2072, Arrays.asList("Espanol"), "+505", "America/Managua"),
            createCountry("PA", "PAN", "Panama", "Panama", "Republica de Panama", "Ciudad de Panama", "America", "America Central", "Balboa panameno", "PAB", "B/.", 4315000L, 75417.0, 8.5380, -80.7821, Arrays.asList("Espanol"), "+507", "America/Panama"),
            createCountry("KN", "KNA", "San Cristobal y Nieves", "Saint Kitts and Nevis", "Federacion de San Cristobal y Nieves", "Basseterre", "America", "Caribe", "Dolar del Caribe Oriental", "XCD", "$", 53199L, 261.0, 17.3578, -62.7830, Arrays.asList("Ingles"), "+1", "America/St_Kitts"),
            createCountry("LC", "LCA", "Santa Lucia", "Saint Lucia", "Santa Lucia", "Castries", "America", "Caribe", "Dolar del Caribe Oriental", "XCD", "$", 183627L, 616.0, 13.9094, -60.9789, Arrays.asList("Ingles"), "+1", "America/St_Lucia"),
            createCountry("VC", "VCT", "San Vicente y las Granadinas", "Saint Vincent and the Grenadines", "San Vicente y las Granadinas", "Kingstown", "America", "Caribe", "Dolar del Caribe Oriental", "XCD", "$", 110940L, 389.0, 12.9843, -61.2872, Arrays.asList("Ingles"), "+1", "America/St_Vincent"),
            createCountry("TT", "TTO", "Trinidad y Tobago", "Trinidad and Tobago", "Republica de Trinidad y Tobago", "Puerto Espana", "America", "Caribe", "Dolar de Trinidad y Tobago", "TTD", "$", 1399000L, 5130.0, 10.6918, -61.2225, Arrays.asList("Ingles"), "+1", "America/Port_of_Spain"),
            createCountry("US", "USA", "Estados Unidos", "United States", "Estados Unidos de America", "Washington D.C.", "America", "America del Norte", "Dolar estadounidense", "USD", "$", 331900000L, 9833520.0, 37.0902, -95.7129, Arrays.asList("Ingles"), "+1", "America/New_York"),

            // ===================== AMERICA DEL SUR (12 paises) =====================
            createCountry("AR", "ARG", "Argentina", "Argentina", "Republica Argentina", "Buenos Aires", "America", "America del Sur", "Peso argentino", "ARS", "$", 45380000L, 2780400.0, -38.4161, -63.6167, Arrays.asList("Espanol"), "+54", "America/Buenos_Aires"),
            createCountry("BO", "BOL", "Bolivia", "Bolivia", "Estado Plurinacional de Bolivia", "Sucre", "America", "America del Sur", "Boliviano", "BOB", "Bs", 11673000L, 1098581.0, -16.2902, -63.5887, Arrays.asList("Espanol", "Quechua", "Aimara"), "+591", "America/La_Paz"),
            createCountry("BR", "BRA", "Brasil", "Brazil", "Republica Federativa del Brasil", "Brasilia", "America", "America del Sur", "Real brasileno", "BRL", "R$", 212600000L, 8515767.0, -14.2350, -51.9253, Arrays.asList("Portugues"), "+55", "America/Sao_Paulo"),
            createCountry("CL", "CHL", "Chile", "Chile", "Republica de Chile", "Santiago", "America", "America del Sur", "Peso chileno", "CLP", "$", 19120000L, 756102.0, -35.6751, -71.5430, Arrays.asList("Espanol"), "+56", "America/Santiago"),
            createCountry("CO", "COL", "Colombia", "Colombia", "Republica de Colombia", "Bogota", "America", "America del Sur", "Peso colombiano", "COP", "$", 50880000L, 1141748.0, 4.5709, -74.2973, Arrays.asList("Espanol"), "+57", "America/Bogota"),
            createCountry("EC", "ECU", "Ecuador", "Ecuador", "Republica del Ecuador", "Quito", "America", "America del Sur", "Dolar estadounidense", "USD", "$", 17643000L, 283561.0, -1.8312, -78.1834, Arrays.asList("Espanol"), "+593", "America/Guayaquil"),
            createCountry("GY", "GUY", "Guyana", "Guyana", "Republica Cooperativa de Guyana", "Georgetown", "America", "America del Sur", "Dolar guyanes", "GYD", "$", 786552L, 214969.0, 4.8604, -58.9302, Arrays.asList("Ingles"), "+592", "America/Guyana"),
            createCountry("PY", "PRY", "Paraguay", "Paraguay", "Republica del Paraguay", "Asuncion", "America", "America del Sur", "Guarani paraguayo", "PYG", "G", 7133000L, 406752.0, -23.4425, -58.4438, Arrays.asList("Espanol", "Guarani"), "+595", "America/Asuncion"),
            createCountry("PE", "PER", "Peru", "Peru", "Republica del Peru", "Lima", "America", "America del Sur", "Sol peruano", "PEN", "S/", 32970000L, 1285216.0, -9.1900, -75.0152, Arrays.asList("Espanol", "Quechua", "Aimara"), "+51", "America/Lima"),
            createCountry("SR", "SUR", "Surinam", "Suriname", "Republica de Surinam", "Paramaribo", "America", "America del Sur", "Dolar surinames", "SRD", "$", 586632L, 163820.0, 3.9193, -56.0278, Arrays.asList("Neerlandes"), "+597", "America/Paramaribo"),
            createCountry("UY", "URY", "Uruguay", "Uruguay", "Republica Oriental del Uruguay", "Montevideo", "America", "America del Sur", "Peso uruguayo", "UYU", "$", 3474000L, 176215.0, -32.5228, -55.7658, Arrays.asList("Espanol"), "+598", "America/Montevideo"),
            createCountry("VE", "VEN", "Venezuela", "Venezuela", "Republica Bolivariana de Venezuela", "Caracas", "America", "America del Sur", "Bolivar soberano", "VES", "Bs", 28440000L, 916445.0, 6.4238, -66.5897, Arrays.asList("Espanol"), "+58", "America/Caracas"),

            // ===================== ASIA (49 paises) =====================
            createCountry("AF", "AFG", "Afganistan", "Afghanistan", "Republica Islamica de Afganistan", "Kabul", "Asia", "Asia del Sur", "Afgani afgano", "AFN", "Af", 38928000L, 652230.0, 33.9391, 67.7100, Arrays.asList("Dari", "Pastun"), "+93", "Asia/Kabul"),
            createCountry("AM", "ARM", "Armenia", "Armenia", "Republica de Armenia", "Erevan", "Asia", "Asia Occidental", "Dram armenio", "AMD", "AMD", 2963000L, 29743.0, 40.0691, 45.0382, Arrays.asList("Armenio"), "+374", "Asia/Yerevan"),
            createCountry("AZ", "AZE", "Azerbaiyan", "Azerbaijan", "Republica de Azerbaiyan", "Baku", "Asia", "Asia Occidental", "Manat azerbaiyano", "AZN", "m", 10139000L, 86600.0, 40.1431, 47.5769, Arrays.asList("Azerbaiyano"), "+994", "Asia/Baku"),
            createCountry("BH", "BHR", "Barein", "Bahrain", "Reino de Barein", "Manama", "Asia", "Asia Occidental", "Dinar bahreni", "BHD", "BD", 1701000L, 765.0, 26.0667, 50.5577, Arrays.asList("Arabe"), "+973", "Asia/Bahrain"),
            createCountry("BD", "BGD", "Bangladesh", "Bangladesh", "Republica Popular de Bangladesh", "Daca", "Asia", "Asia del Sur", "Taka bangladesi", "BDT", "Tk", 164700000L, 147570.0, 23.6850, 90.3563, Arrays.asList("Bengali"), "+880", "Asia/Dhaka"),
            createCountry("BT", "BTN", "Butan", "Bhutan", "Reino de Butan", "Timbu", "Asia", "Asia del Sur", "Ngultrum butanes", "BTN", "Nu", 771608L, 38394.0, 27.5142, 90.4336, Arrays.asList("Dzongkha"), "+975", "Asia/Thimphu"),
            createCountry("BN", "BRN", "Brunei", "Brunei", "Estado de Brunei Darussalam", "Bandar Seri Begawan", "Asia", "Sudeste Asiatico", "Dolar de Brunei", "BND", "$", 437479L, 5765.0, 4.5353, 114.7277, Arrays.asList("Malayo"), "+673", "Asia/Brunei"),
            createCountry("KH", "KHM", "Camboya", "Cambodia", "Reino de Camboya", "Nom Pen", "Asia", "Sudeste Asiatico", "Riel camboyano", "KHR", "KHR", 16719000L, 181035.0, 12.5657, 104.9910, Arrays.asList("Jemer"), "+855", "Asia/Phnom_Penh"),
            createCountry("CN", "CHN", "China", "China", "Republica Popular China", "Pekin", "Asia", "Asia Oriental", "Yuan chino", "CNY", "Y", 1402000000L, 9596961.0, 35.8617, 104.1954, Arrays.asList("Chino mandarin"), "+86", "Asia/Shanghai"),
            createCountry("GE", "GEO", "Georgia", "Georgia", "Georgia", "Tiflis", "Asia", "Asia Occidental", "Lari georgiano", "GEL", "L", 3714000L, 69700.0, 42.3154, 43.3569, Arrays.asList("Georgiano"), "+995", "Asia/Tbilisi"),
            createCountry("IN", "IND", "India", "India", "Republica de la India", "Nueva Delhi", "Asia", "Asia del Sur", "Rupia india", "INR", "Rs", 1380000000L, 3287263.0, 20.5937, 78.9629, Arrays.asList("Hindi", "Ingles"), "+91", "Asia/Kolkata"),
            createCountry("ID", "IDN", "Indonesia", "Indonesia", "Republica de Indonesia", "Yakarta", "Asia", "Sudeste Asiatico", "Rupia indonesia", "IDR", "Rp", 273500000L, 1904569.0, -0.7893, 113.9213, Arrays.asList("Indonesio"), "+62", "Asia/Jakarta"),
            createCountry("IR", "IRN", "Iran", "Iran", "Republica Islamica de Iran", "Teheran", "Asia", "Asia Occidental", "Rial irani", "IRR", "Rl", 83993000L, 1648195.0, 32.4279, 53.6880, Arrays.asList("Persa"), "+98", "Asia/Tehran"),
            createCountry("IQ", "IRQ", "Irak", "Iraq", "Republica de Irak", "Bagdad", "Asia", "Asia Occidental", "Dinar iraqui", "IQD", "ID", 40222000L, 438317.0, 33.2232, 43.6793, Arrays.asList("Arabe", "Kurdo"), "+964", "Asia/Baghdad"),
            createCountry("IL", "ISR", "Israel", "Israel", "Estado de Israel", "Jerusalen", "Asia", "Asia Occidental", "Nuevo sekel israeli", "ILS", "NIS", 9217000L, 20770.0, 31.0461, 34.8516, Arrays.asList("Hebreo", "Arabe"), "+972", "Asia/Jerusalem"),
            createCountry("JP", "JPN", "Japon", "Japan", "Japon", "Tokio", "Asia", "Asia Oriental", "Yen japones", "JPY", "Y", 125800000L, 377975.0, 36.2048, 138.2529, Arrays.asList("Japones"), "+81", "Asia/Tokyo"),
            createCountry("JO", "JOR", "Jordania", "Jordan", "Reino Hachemita de Jordania", "Aman", "Asia", "Asia Occidental", "Dinar jordano", "JOD", "JD", 10203000L, 89342.0, 30.5852, 36.2384, Arrays.asList("Arabe"), "+962", "Asia/Amman"),
            createCountry("KZ", "KAZ", "Kazajistan", "Kazakhstan", "Republica de Kazajistan", "Nur-Sultan", "Asia", "Asia Central", "Tenge kazajo", "KZT", "T", 18776000L, 2724900.0, 48.0196, 66.9237, Arrays.asList("Kazajo", "Ruso"), "+7", "Asia/Almaty"),
            createCountry("KW", "KWT", "Kuwait", "Kuwait", "Estado de Kuwait", "Ciudad de Kuwait", "Asia", "Asia Occidental", "Dinar kuwaiti", "KWD", "KD", 4270000L, 17818.0, 29.3117, 47.4818, Arrays.asList("Arabe"), "+965", "Asia/Kuwait"),
            createCountry("KG", "KGZ", "Kirguistan", "Kyrgyzstan", "Republica Kirguisa", "Biskek", "Asia", "Asia Central", "Som kirguis", "KGS", "C", 6524000L, 199951.0, 41.2044, 74.7661, Arrays.asList("Kirguis", "Ruso"), "+996", "Asia/Bishkek"),
            createCountry("LA", "LAO", "Laos", "Laos", "Republica Democratica Popular Lao", "Vientian", "Asia", "Sudeste Asiatico", "Kip laosiano", "LAK", "KN", 7276000L, 236800.0, 19.8563, 102.4955, Arrays.asList("Lao"), "+856", "Asia/Vientiane"),
            createCountry("LB", "LBN", "Libano", "Lebanon", "Republica Libanesa", "Beirut", "Asia", "Asia Occidental", "Libra libanesa", "LBP", "LL", 6825000L, 10452.0, 33.8547, 35.8623, Arrays.asList("Arabe"), "+961", "Asia/Beirut"),
            createCountry("MY", "MYS", "Malasia", "Malaysia", "Malasia", "Kuala Lumpur", "Asia", "Sudeste Asiatico", "Ringgit malayo", "MYR", "RM", 32370000L, 330803.0, 4.2105, 101.9758, Arrays.asList("Malayo"), "+60", "Asia/Kuala_Lumpur"),
            createCountry("MV", "MDV", "Maldivas", "Maldives", "Republica de Maldivas", "Male", "Asia", "Asia del Sur", "Rufiyaa maldiva", "MVR", "Rf", 540544L, 300.0, 3.2028, 73.2207, Arrays.asList("Maldivo"), "+960", "Indian/Maldives"),
            createCountry("MN", "MNG", "Mongolia", "Mongolia", "Mongolia", "Ulan Bator", "Asia", "Asia Oriental", "Tugrik mongol", "MNT", "T", 3278000L, 1564116.0, 46.8625, 103.8467, Arrays.asList("Mongol"), "+976", "Asia/Ulaanbaatar"),
            createCountry("MM", "MMR", "Myanmar", "Myanmar", "Republica de la Union de Myanmar", "Naipyido", "Asia", "Sudeste Asiatico", "Kyat birmano", "MMK", "K", 54410000L, 676578.0, 21.9162, 95.9560, Arrays.asList("Birmano"), "+95", "Asia/Yangon"),
            createCountry("NP", "NPL", "Nepal", "Nepal", "Republica Democratica Federal de Nepal", "Katmandu", "Asia", "Asia del Sur", "Rupia nepali", "NPR", "Rs", 29137000L, 147181.0, 28.3949, 84.1240, Arrays.asList("Nepali"), "+977", "Asia/Kathmandu"),
            createCountry("KP", "PRK", "Corea del Norte", "North Korea", "Republica Popular Democratica de Corea", "Pionyang", "Asia", "Asia Oriental", "Won norcoreano", "KPW", "W", 25778000L, 120538.0, 40.3399, 127.5101, Arrays.asList("Coreano"), "+850", "Asia/Pyongyang"),
            createCountry("OM", "OMN", "Oman", "Oman", "Sultanato de Oman", "Mascate", "Asia", "Asia Occidental", "Rial omani", "OMR", "RO", 5106000L, 309500.0, 21.4735, 55.9754, Arrays.asList("Arabe"), "+968", "Asia/Muscat"),
            createCountry("PK", "PAK", "Pakistan", "Pakistan", "Republica Islamica de Pakistan", "Islamabad", "Asia", "Asia del Sur", "Rupia paquistani", "PKR", "Rs", 220900000L, 881913.0, 30.3753, 69.3451, Arrays.asList("Urdu", "Ingles"), "+92", "Asia/Karachi"),
            createCountry("PS", "PSE", "Palestina", "Palestine", "Estado de Palestina", "Ramala", "Asia", "Asia Occidental", "Sekel israeli", "ILS", "NIS", 5101000L, 6220.0, 31.9522, 35.2332, Arrays.asList("Arabe"), "+970", "Asia/Gaza"),
            createCountry("PH", "PHL", "Filipinas", "Philippines", "Republica de Filipinas", "Manila", "Asia", "Sudeste Asiatico", "Peso filipino", "PHP", "P", 109600000L, 300000.0, 12.8797, 121.7740, Arrays.asList("Filipino", "Ingles"), "+63", "Asia/Manila"),
            createCountry("QA", "QAT", "Catar", "Qatar", "Estado de Catar", "Doha", "Asia", "Asia Occidental", "Rial catari", "QAR", "QR", 2881000L, 11586.0, 25.3548, 51.1839, Arrays.asList("Arabe"), "+974", "Asia/Qatar"),
            createCountry("SA", "SAU", "Arabia Saudita", "Saudi Arabia", "Reino de Arabia Saudita", "Riad", "Asia", "Asia Occidental", "Rial saudi", "SAR", "SR", 34810000L, 2149690.0, 23.8859, 45.0792, Arrays.asList("Arabe"), "+966", "Asia/Riyadh"),
            createCountry("SG", "SGP", "Singapur", "Singapore", "Republica de Singapur", "Singapur", "Asia", "Sudeste Asiatico", "Dolar de Singapur", "SGD", "$", 5850000L, 728.6, 1.3521, 103.8198, Arrays.asList("Ingles", "Malayo", "Mandarin", "Tamil"), "+65", "Asia/Singapore"),
            createCountry("KR", "KOR", "Corea del Sur", "South Korea", "Republica de Corea", "Seul", "Asia", "Asia Oriental", "Won surcoreano", "KRW", "W", 51780000L, 100210.0, 35.9078, 127.7669, Arrays.asList("Coreano"), "+82", "Asia/Seoul"),
            createCountry("LK", "LKA", "Sri Lanka", "Sri Lanka", "Republica Democratica Socialista de Sri Lanka", "Sri Jayawardenapura Kotte", "Asia", "Asia del Sur", "Rupia de Sri Lanka", "LKR", "Rs", 21413000L, 65610.0, 7.8731, 80.7718, Arrays.asList("Cingales", "Tamil"), "+94", "Asia/Colombo"),
            createCountry("SY", "SYR", "Siria", "Syria", "Republica Arabe Siria", "Damasco", "Asia", "Asia Occidental", "Libra siria", "SYP", "LS", 17500000L, 185180.0, 34.8021, 38.9968, Arrays.asList("Arabe"), "+963", "Asia/Damascus"),
            createCountry("TW", "TWN", "Taiwan", "Taiwan", "Republica de China", "Taipei", "Asia", "Asia Oriental", "Nuevo dolar taiwanes", "TWD", "NT$", 23816000L, 36193.0, 23.6978, 120.9605, Arrays.asList("Chino mandarin"), "+886", "Asia/Taipei"),
            createCountry("TJ", "TJK", "Tayikistan", "Tajikistan", "Republica de Tayikistan", "Dushanbe", "Asia", "Asia Central", "Somoni tayiko", "TJS", "SM", 9537000L, 143100.0, 38.8610, 71.2761, Arrays.asList("Tayiko"), "+992", "Asia/Dushanbe"),
            createCountry("TH", "THA", "Tailandia", "Thailand", "Reino de Tailandia", "Bangkok", "Asia", "Sudeste Asiatico", "Baht tailandes", "THB", "B", 69800000L, 513120.0, 15.8700, 100.9925, Arrays.asList("Tailandes"), "+66", "Asia/Bangkok"),
            createCountry("TL", "TLS", "Timor Oriental", "Timor-Leste", "Republica Democratica de Timor Oriental", "Dili", "Asia", "Sudeste Asiatico", "Dolar estadounidense", "USD", "$", 1318000L, 14874.0, -8.8742, 125.7275, Arrays.asList("Tetum", "Portugues"), "+670", "Asia/Dili"),
            createCountry("TR", "TUR", "Turquia", "Turkey", "Republica de Turquia", "Ankara", "Asia", "Asia Occidental", "Lira turca", "TRY", "TL", 84340000L, 783562.0, 38.9637, 35.2433, Arrays.asList("Turco"), "+90", "Europe/Istanbul"),
            createCountry("TM", "TKM", "Turkmenistan", "Turkmenistan", "Turkmenistan", "Asjabad", "Asia", "Asia Central", "Manat turkmeno", "TMT", "T", 6031000L, 488100.0, 38.9697, 59.5563, Arrays.asList("Turkmeno"), "+993", "Asia/Ashgabat"),
            createCountry("AE", "ARE", "Emiratos Arabes Unidos", "United Arab Emirates", "Emiratos Arabes Unidos", "Abu Dabi", "Asia", "Asia Occidental", "Dirham de los EAU", "AED", "Dh", 9890000L, 83600.0, 23.4241, 53.8478, Arrays.asList("Arabe"), "+971", "Asia/Dubai"),
            createCountry("UZ", "UZB", "Uzbekistan", "Uzbekistan", "Republica de Uzbekistan", "Taskent", "Asia", "Asia Central", "Som uzbeko", "UZS", "som", 33469000L, 447400.0, 41.3775, 64.5853, Arrays.asList("Uzbeko"), "+998", "Asia/Tashkent"),
            createCountry("VN", "VNM", "Vietnam", "Vietnam", "Republica Socialista de Vietnam", "Hanoi", "Asia", "Sudeste Asiatico", "Dong vietnamita", "VND", "D", 97339000L, 331212.0, 14.0583, 108.2772, Arrays.asList("Vietnamita"), "+84", "Asia/Ho_Chi_Minh"),
            createCountry("YE", "YEM", "Yemen", "Yemen", "Republica de Yemen", "Sana", "Asia", "Asia Occidental", "Rial yemeni", "YER", "YR", 29826000L, 527968.0, 15.5527, 48.5164, Arrays.asList("Arabe"), "+967", "Asia/Aden"),

            // ===================== AFRICA (54 paises) =====================
            createCountry("DZ", "DZA", "Argelia", "Algeria", "Republica Argelina Democratica y Popular", "Argel", "Africa", "Africa del Norte", "Dinar argelino", "DZD", "DA", 43851000L, 2381741.0, 28.0339, 1.6596, Arrays.asList("Arabe", "Bereber"), "+213", "Africa/Algiers"),
            createCountry("AO", "AGO", "Angola", "Angola", "Republica de Angola", "Luanda", "Africa", "Africa Central", "Kwanza angoleno", "AOA", "Kz", 32866000L, 1246700.0, -11.2027, 17.8739, Arrays.asList("Portugues"), "+244", "Africa/Luanda"),
            createCountry("BJ", "BEN", "Benin", "Benin", "Republica de Benin", "Porto Novo", "Africa", "Africa Occidental", "Franco CFA", "XOF", "Fr", 12123000L, 114763.0, 9.3077, 2.3158, Arrays.asList("Frances"), "+229", "Africa/Porto-Novo"),
            createCountry("BW", "BWA", "Botsuana", "Botswana", "Republica de Botsuana", "Gaborone", "Africa", "Africa del Sur", "Pula botsuano", "BWP", "P", 2352000L, 581730.0, -22.3285, 24.6849, Arrays.asList("Ingles", "Setsuana"), "+267", "Africa/Gaborone"),
            createCountry("BF", "BFA", "Burkina Faso", "Burkina Faso", "Burkina Faso", "Uagadugu", "Africa", "Africa Occidental", "Franco CFA", "XOF", "Fr", 20903000L, 272967.0, 12.2383, -1.5616, Arrays.asList("Frances"), "+226", "Africa/Ouagadougou"),
            createCountry("BI", "BDI", "Burundi", "Burundi", "Republica de Burundi", "Gitega", "Africa", "Africa Oriental", "Franco de Burundi", "BIF", "Fr", 11891000L, 27834.0, -3.3731, 29.9189, Arrays.asList("Kirundi", "Frances"), "+257", "Africa/Bujumbura"),
            createCountry("CV", "CPV", "Cabo Verde", "Cape Verde", "Republica de Cabo Verde", "Praia", "Africa", "Africa Occidental", "Escudo caboverdiano", "CVE", "$", 555987L, 4033.0, 16.5388, -23.0418, Arrays.asList("Portugues"), "+238", "Atlantic/Cape_Verde"),
            createCountry("CM", "CMR", "Camerun", "Cameroon", "Republica de Camerun", "Yaounde", "Africa", "Africa Central", "Franco CFA", "XAF", "Fr", 26545000L, 475442.0, 7.3697, 12.3547, Arrays.asList("Frances", "Ingles"), "+237", "Africa/Douala"),
            createCountry("CF", "CAF", "Republica Centroafricana", "Central African Republic", "Republica Centroafricana", "Bangui", "Africa", "Africa Central", "Franco CFA", "XAF", "Fr", 4830000L, 622984.0, 6.6111, 20.9394, Arrays.asList("Frances", "Sango"), "+236", "Africa/Bangui"),
            createCountry("TD", "TCD", "Chad", "Chad", "Republica del Chad", "Yamena", "Africa", "Africa Central", "Franco CFA", "XAF", "Fr", 16425000L, 1284000.0, 15.4542, 18.7322, Arrays.asList("Frances", "Arabe"), "+235", "Africa/Ndjamena"),
            createCountry("KM", "COM", "Comoras", "Comoros", "Union de las Comoras", "Moroni", "Africa", "Africa Oriental", "Franco comorense", "KMF", "Fr", 869601L, 1862.0, -11.6455, 43.3333, Arrays.asList("Arabe", "Frances", "Comorense"), "+269", "Indian/Comoro"),
            createCountry("CG", "COG", "Republica del Congo", "Republic of the Congo", "Republica del Congo", "Brazzaville", "Africa", "Africa Central", "Franco CFA", "XAF", "Fr", 5518000L, 342000.0, -0.2280, 15.8277, Arrays.asList("Frances"), "+242", "Africa/Brazzaville"),
            createCountry("CD", "COD", "Republica Democratica del Congo", "Democratic Republic of the Congo", "Republica Democratica del Congo", "Kinshasa", "Africa", "Africa Central", "Franco congoleno", "CDF", "Fr", 89561000L, 2344858.0, -4.0383, 21.7587, Arrays.asList("Frances"), "+243", "Africa/Kinshasa"),
            createCountry("CI", "CIV", "Costa de Marfil", "Ivory Coast", "Republica de Costa de Marfil", "Yamusukro", "Africa", "Africa Occidental", "Franco CFA", "XOF", "Fr", 26378000L, 322463.0, 7.5400, -5.5471, Arrays.asList("Frances"), "+225", "Africa/Abidjan"),
            createCountry("DJ", "DJI", "Yibuti", "Djibouti", "Republica de Yibuti", "Yibuti", "Africa", "Africa Oriental", "Franco yibutiano", "DJF", "Fr", 988000L, 23200.0, 11.8251, 42.5903, Arrays.asList("Frances", "Arabe"), "+253", "Africa/Djibouti"),
            createCountry("EG", "EGY", "Egipto", "Egypt", "Republica Arabe de Egipto", "El Cairo", "Africa", "Africa del Norte", "Libra egipcia", "EGP", "lb", 102300000L, 1002450.0, 26.8206, 30.8025, Arrays.asList("Arabe"), "+20", "Africa/Cairo"),
            createCountry("GQ", "GNQ", "Guinea Ecuatorial", "Equatorial Guinea", "Republica de Guinea Ecuatorial", "Malabo", "Africa", "Africa Central", "Franco CFA", "XAF", "Fr", 1403000L, 28051.0, 1.6508, 10.2679, Arrays.asList("Espanol", "Frances", "Portugues"), "+240", "Africa/Malabo"),
            createCountry("ER", "ERI", "Eritrea", "Eritrea", "Estado de Eritrea", "Asmara", "Africa", "Africa Oriental", "Nakfa eritreo", "ERN", "Nkf", 3546000L, 117600.0, 15.1794, 39.7823, Arrays.asList("Tigrina", "Arabe"), "+291", "Africa/Asmara"),
            createCountry("SZ", "SWZ", "Esuatini", "Eswatini", "Reino de Esuatini", "Mbabane", "Africa", "Africa del Sur", "Lilangeni suazi", "SZL", "L", 1160000L, 17364.0, -26.5225, 31.4659, Arrays.asList("Ingles", "Suazi"), "+268", "Africa/Mbabane"),
            createCountry("ET", "ETH", "Etiopia", "Ethiopia", "Republica Democratica Federal de Etiopia", "Adis Abeba", "Africa", "Africa Oriental", "Birr etiope", "ETB", "Br", 115000000L, 1104300.0, 9.1450, 40.4897, Arrays.asList("Amharico"), "+251", "Africa/Addis_Ababa"),
            createCountry("GA", "GAB", "Gabon", "Gabon", "Republica Gabonesa", "Libreville", "Africa", "Africa Central", "Franco CFA", "XAF", "Fr", 2226000L, 267668.0, -0.8037, 11.6094, Arrays.asList("Frances"), "+241", "Africa/Libreville"),
            createCountry("GM", "GMB", "Gambia", "Gambia", "Republica de Gambia", "Banjul", "Africa", "Africa Occidental", "Dalasi gambiano", "GMD", "D", 2417000L, 11295.0, 13.4432, -15.3101, Arrays.asList("Ingles"), "+220", "Africa/Banjul"),
            createCountry("GH", "GHA", "Ghana", "Ghana", "Republica de Ghana", "Accra", "Africa", "Africa Occidental", "Cedi ganes", "GHS", "GH", 31073000L, 238533.0, 7.9465, -1.0232, Arrays.asList("Ingles"), "+233", "Africa/Accra"),
            createCountry("GN", "GIN", "Guinea", "Guinea", "Republica de Guinea", "Conakry", "Africa", "Africa Occidental", "Franco guineano", "GNF", "Fr", 13132000L, 245857.0, 9.9456, -9.6966, Arrays.asList("Frances"), "+224", "Africa/Conakry"),
            createCountry("GW", "GNB", "Guinea-Bisau", "Guinea-Bissau", "Republica de Guinea-Bisau", "Bisau", "Africa", "Africa Occidental", "Franco CFA", "XOF", "Fr", 1968000L, 36125.0, 11.8037, -15.1804, Arrays.asList("Portugues"), "+245", "Africa/Bissau"),
            createCountry("KE", "KEN", "Kenia", "Kenya", "Republica de Kenia", "Nairobi", "Africa", "Africa Oriental", "Chelin keniano", "KES", "KSh", 53770000L, 580367.0, -0.0236, 37.9062, Arrays.asList("Suajili", "Ingles"), "+254", "Africa/Nairobi"),
            createCountry("LS", "LSO", "Lesoto", "Lesotho", "Reino de Lesoto", "Maseru", "Africa", "Africa del Sur", "Loti de Lesoto", "LSL", "L", 2142000L, 30355.0, -29.6100, 28.2336, Arrays.asList("Sesotho", "Ingles"), "+266", "Africa/Maseru"),
            createCountry("LR", "LBR", "Liberia", "Liberia", "Republica de Liberia", "Monrovia", "Africa", "Africa Occidental", "Dolar liberiano", "LRD", "$", 5058000L, 111369.0, 6.4281, -9.4295, Arrays.asList("Ingles"), "+231", "Africa/Monrovia"),
            createCountry("LY", "LBY", "Libia", "Libya", "Estado de Libia", "Tripoli", "Africa", "Africa del Norte", "Dinar libio", "LYD", "LD", 6871000L, 1759540.0, 26.3351, 17.2283, Arrays.asList("Arabe"), "+218", "Africa/Tripoli"),
            createCountry("MG", "MDG", "Madagascar", "Madagascar", "Republica de Madagascar", "Antananarivo", "Africa", "Africa Oriental", "Ariary malgache", "MGA", "Ar", 27691000L, 587041.0, -18.7669, 46.8691, Arrays.asList("Malgache", "Frances"), "+261", "Indian/Antananarivo"),
            createCountry("MW", "MWI", "Malaui", "Malawi", "Republica de Malaui", "Lilongue", "Africa", "Africa Oriental", "Kwacha malauiano", "MWK", "MK", 19130000L, 118484.0, -13.2543, 34.3015, Arrays.asList("Ingles", "Chichewa"), "+265", "Africa/Blantyre"),
            createCountry("ML", "MLI", "Mali", "Mali", "Republica de Mali", "Bamako", "Africa", "Africa Occidental", "Franco CFA", "XOF", "Fr", 20251000L, 1240192.0, 17.5707, -3.9962, Arrays.asList("Frances"), "+223", "Africa/Bamako"),
            createCountry("MR", "MRT", "Mauritania", "Mauritania", "Republica Islamica de Mauritania", "Nuakchot", "Africa", "Africa Occidental", "Uguiya mauritana", "MRU", "UM", 4650000L, 1030700.0, 21.0079, -10.9408, Arrays.asList("Arabe"), "+222", "Africa/Nouakchott"),
            createCountry("MU", "MUS", "Mauricio", "Mauritius", "Republica de Mauricio", "Port Louis", "Africa", "Africa Oriental", "Rupia mauriciana", "MUR", "Rs", 1272000L, 2040.0, -20.3484, 57.5522, Arrays.asList("Ingles"), "+230", "Indian/Mauritius"),
            createCountry("MA", "MAR", "Marruecos", "Morocco", "Reino de Marruecos", "Rabat", "Africa", "Africa del Norte", "Dirham marroqui", "MAD", "DH", 36910000L, 446550.0, 31.7917, -7.0926, Arrays.asList("Arabe", "Bereber"), "+212", "Africa/Casablanca"),
            createCountry("MZ", "MOZ", "Mozambique", "Mozambique", "Republica de Mozambique", "Maputo", "Africa", "Africa Oriental", "Metical mozambiqueno", "MZN", "MT", 31255000L, 801590.0, -18.6657, 35.5296, Arrays.asList("Portugues"), "+258", "Africa/Maputo"),
            createCountry("NA", "NAM", "Namibia", "Namibia", "Republica de Namibia", "Windhoek", "Africa", "Africa del Sur", "Dolar namibio", "NAD", "$", 2541000L, 825615.0, -22.9576, 18.4904, Arrays.asList("Ingles"), "+264", "Africa/Windhoek"),
            createCountry("NE", "NER", "Niger", "Niger", "Republica del Niger", "Niamey", "Africa", "Africa Occidental", "Franco CFA", "XOF", "Fr", 24207000L, 1267000.0, 17.6078, 8.0817, Arrays.asList("Frances"), "+227", "Africa/Niamey"),
            createCountry("NG", "NGA", "Nigeria", "Nigeria", "Republica Federal de Nigeria", "Abuya", "Africa", "Africa Occidental", "Naira nigeriano", "NGN", "N", 206140000L, 923768.0, 9.0820, 8.6753, Arrays.asList("Ingles"), "+234", "Africa/Lagos"),
            createCountry("RW", "RWA", "Ruanda", "Rwanda", "Republica de Ruanda", "Kigali", "Africa", "Africa Oriental", "Franco ruandes", "RWF", "Fr", 12952000L, 26338.0, -1.9403, 29.8739, Arrays.asList("Kinyarwanda", "Frances", "Ingles"), "+250", "Africa/Kigali"),
            createCountry("ST", "STP", "Santo Tome y Principe", "Sao Tome and Principe", "Republica Democratica de Santo Tome y Principe", "Santo Tome", "Africa", "Africa Central", "Dobra santotomense", "STN", "Db", 219159L, 964.0, 0.1864, 6.6131, Arrays.asList("Portugues"), "+239", "Africa/Sao_Tome"),
            createCountry("SN", "SEN", "Senegal", "Senegal", "Republica de Senegal", "Dakar", "Africa", "Africa Occidental", "Franco CFA", "XOF", "Fr", 16744000L, 196722.0, 14.4974, -14.4524, Arrays.asList("Frances"), "+221", "Africa/Dakar"),
            createCountry("SC", "SYC", "Seychelles", "Seychelles", "Republica de Seychelles", "Victoria", "Africa", "Africa Oriental", "Rupia seychellense", "SCR", "Rs", 98347L, 459.0, -4.6796, 55.4920, Arrays.asList("Criollo seychellense", "Ingles", "Frances"), "+248", "Indian/Mahe"),
            createCountry("SL", "SLE", "Sierra Leona", "Sierra Leone", "Republica de Sierra Leona", "Freetown", "Africa", "Africa Occidental", "Leone sierraleonense", "SLL", "Le", 7976000L, 71740.0, 8.4606, -11.7799, Arrays.asList("Ingles"), "+232", "Africa/Freetown"),
            createCountry("SO", "SOM", "Somalia", "Somalia", "Republica Federal de Somalia", "Mogadiscio", "Africa", "Africa Oriental", "Chelin somali", "SOS", "Sh", 15893000L, 637657.0, 5.1521, 46.1996, Arrays.asList("Somali", "Arabe"), "+252", "Africa/Mogadishu"),
            createCountry("ZA", "ZAF", "Sudafrica", "South Africa", "Republica de Sudafrica", "Pretoria", "Africa", "Africa del Sur", "Rand sudafricano", "ZAR", "R", 59310000L, 1221037.0, -30.5595, 22.9375, Arrays.asList("Zulu", "Xhosa", "Afrikaans", "Ingles"), "+27", "Africa/Johannesburg"),
            createCountry("SS", "SSD", "Sudan del Sur", "South Sudan", "Republica de Sudan del Sur", "Yuba", "Africa", "Africa Oriental", "Libra sursudanesa", "SSP", "lb", 11194000L, 644329.0, 6.8770, 31.3070, Arrays.asList("Ingles"), "+211", "Africa/Juba"),
            createCountry("SD", "SDN", "Sudan", "Sudan", "Republica del Sudan", "Jartum", "Africa", "Africa del Norte", "Libra sudanesa", "SDG", "lb", 43849000L, 1861484.0, 12.8628, 30.2176, Arrays.asList("Arabe", "Ingles"), "+249", "Africa/Khartoum"),
            createCountry("TZ", "TZA", "Tanzania", "Tanzania", "Republica Unida de Tanzania", "Dodoma", "Africa", "Africa Oriental", "Chelin tanzano", "TZS", "Sh", 59734000L, 947303.0, -6.3690, 34.8888, Arrays.asList("Suajili", "Ingles"), "+255", "Africa/Dar_es_Salaam"),
            createCountry("TG", "TGO", "Togo", "Togo", "Republica Togolesa", "Lome", "Africa", "Africa Occidental", "Franco CFA", "XOF", "Fr", 8279000L, 56785.0, 8.6195, 0.8248, Arrays.asList("Frances"), "+228", "Africa/Lome"),
            createCountry("TN", "TUN", "Tunez", "Tunisia", "Republica Tunecina", "Tunez", "Africa", "Africa del Norte", "Dinar tunecino", "TND", "DT", 11819000L, 163610.0, 33.8869, 9.5375, Arrays.asList("Arabe"), "+216", "Africa/Tunis"),
            createCountry("UG", "UGA", "Uganda", "Uganda", "Republica de Uganda", "Kampala", "Africa", "Africa Oriental", "Chelin ugandes", "UGX", "Sh", 45741000L, 241550.0, 1.3733, 32.2903, Arrays.asList("Ingles", "Suajili"), "+256", "Africa/Kampala"),
            createCountry("ZM", "ZMB", "Zambia", "Zambia", "Republica de Zambia", "Lusaka", "Africa", "Africa Oriental", "Kwacha zambiano", "ZMW", "ZK", 18384000L, 752612.0, -13.1339, 27.8493, Arrays.asList("Ingles"), "+260", "Africa/Lusaka"),
            createCountry("ZW", "ZWE", "Zimbabue", "Zimbabwe", "Republica de Zimbabue", "Harare", "Africa", "Africa Oriental", "Dolar zimbabuense", "ZWL", "$", 14863000L, 390757.0, -19.0154, 29.1549, Arrays.asList("Ingles", "Shona", "Ndebele"), "+263", "Africa/Harare"),

            // ===================== OCEANIA (14 paises) =====================
            createCountry("AU", "AUS", "Australia", "Australia", "Mancomunidad de Australia", "Canberra", "Oceania", "Australasia", "Dolar australiano", "AUD", "$", 25690000L, 7692024.0, -25.2744, 133.7751, Arrays.asList("Ingles"), "+61", "Australia/Sydney"),
            createCountry("FJ", "FJI", "Fiyi", "Fiji", "Republica de Fiyi", "Suva", "Oceania", "Melanesia", "Dolar fiyiano", "FJD", "$", 896445L, 18272.0, -17.7134, 178.0650, Arrays.asList("Ingles", "Fiyiano", "Hindi"), "+679", "Pacific/Fiji"),
            createCountry("KI", "KIR", "Kiribati", "Kiribati", "Republica de Kiribati", "Tarawa Sur", "Oceania", "Micronesia", "Dolar australiano", "AUD", "$", 119449L, 811.0, -3.3704, -168.7340, Arrays.asList("Ingles", "Gilbertino"), "+686", "Pacific/Tarawa"),
            createCountry("MH", "MHL", "Islas Marshall", "Marshall Islands", "Republica de las Islas Marshall", "Majuro", "Oceania", "Micronesia", "Dolar estadounidense", "USD", "$", 59190L, 181.0, 7.1315, 171.1845, Arrays.asList("Marshalles", "Ingles"), "+692", "Pacific/Majuro"),
            createCountry("FM", "FSM", "Micronesia", "Micronesia", "Estados Federados de Micronesia", "Palikir", "Oceania", "Micronesia", "Dolar estadounidense", "USD", "$", 115023L, 702.0, 7.4256, 150.5508, Arrays.asList("Ingles"), "+691", "Pacific/Pohnpei"),
            createCountry("NR", "NRU", "Nauru", "Nauru", "Republica de Nauru", "Yaren", "Oceania", "Micronesia", "Dolar australiano", "AUD", "$", 10824L, 21.0, -0.5228, 166.9315, Arrays.asList("Nauruano", "Ingles"), "+674", "Pacific/Nauru"),
            createCountry("NZ", "NZL", "Nueva Zelanda", "New Zealand", "Nueva Zelanda", "Wellington", "Oceania", "Australasia", "Dolar neozelandes", "NZD", "$", 5084000L, 268021.0, -40.9006, 174.8860, Arrays.asList("Ingles", "Maori"), "+64", "Pacific/Auckland"),
            createCountry("PW", "PLW", "Palaos", "Palau", "Republica de Palaos", "Ngerulmud", "Oceania", "Micronesia", "Dolar estadounidense", "USD", "$", 18094L, 459.0, 7.5150, 134.5825, Arrays.asList("Palauano", "Ingles"), "+680", "Pacific/Palau"),
            createCountry("PG", "PNG", "Papua Nueva Guinea", "Papua New Guinea", "Estado Independiente de Papua Nueva Guinea", "Port Moresby", "Oceania", "Melanesia", "Kina papuano", "PGK", "K", 8947000L, 462840.0, -6.3150, 143.9555, Arrays.asList("Tok Pisin", "Ingles", "Hiri Motu"), "+675", "Pacific/Port_Moresby"),
            createCountry("WS", "WSM", "Samoa", "Samoa", "Estado Independiente de Samoa", "Apia", "Oceania", "Polinesia", "Tala samoano", "WST", "T", 198414L, 2842.0, -13.7590, -172.1046, Arrays.asList("Samoano", "Ingles"), "+685", "Pacific/Apia"),
            createCountry("SB", "SLB", "Islas Salomon", "Solomon Islands", "Islas Salomon", "Honiara", "Oceania", "Melanesia", "Dolar de las Islas Salomon", "SBD", "$", 686884L, 28896.0, -9.6457, 160.1562, Arrays.asList("Ingles"), "+677", "Pacific/Guadalcanal"),
            createCountry("TO", "TON", "Tonga", "Tonga", "Reino de Tonga", "Nukualofa", "Oceania", "Polinesia", "Paanga tongano", "TOP", "T$", 105695L, 747.0, -21.1789, -175.1982, Arrays.asList("Tongano", "Ingles"), "+676", "Pacific/Tongatapu"),
            createCountry("TV", "TUV", "Tuvalu", "Tuvalu", "Tuvalu", "Funafuti", "Oceania", "Polinesia", "Dolar australiano", "AUD", "$", 11792L, 26.0, -7.1095, 179.1940, Arrays.asList("Tuvaluano", "Ingles"), "+688", "Pacific/Funafuti"),
            createCountry("VU", "VUT", "Vanuatu", "Vanuatu", "Republica de Vanuatu", "Port Vila", "Oceania", "Melanesia", "Vatu vanuatuense", "VUV", "Vt", 307145L, 12189.0, -15.3767, 166.9592, Arrays.asList("Bislama", "Ingles", "Frances"), "+678", "Pacific/Efate")
        );

        countryRepository.saveAll(countries);
        log.info("Se han guardado {} paises en la base de datos", countries.size());
    }

    private Country createCountry(String isoCode, String isoCode3, String name, String nameEn, String officialName,
            String capital, String continent, String region, String currencyName, String currencyCode,
            String currencySymbol, Long population, Double areaSqKm,
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
                .flagEmoji(isoCode)
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
        int questionsGenerated = 0;

        for (Country country : countries) {
            // Pregunta de capital
            TriviaQuestion capitalQ = TriviaQuestion.builder()
                    .questionType(QuestionType.CAPITAL)
                    .country(country)
                    .questionText("Cual es la capital de " + country.getName() + "?")
                    .correctAnswer(country.getCapital())
                    .wrongOptions(getWrongCapitals(country, countries))
                    .difficulty(1)
                    .points(10)
                    .timeLimitSeconds(15)
                    .explanation(country.getCapital() + " es la capital de " + country.getName() + ".")
                    .active(true)
                    .build();
            triviaQuestionRepository.save(capitalQ);
            questionsGenerated++;

            // Pregunta de bandera
            TriviaQuestion flagQ = TriviaQuestion.builder()
                    .questionType(QuestionType.FLAG)
                    .country(country)
                    .questionText("A que pais pertenece esta bandera?")
                    .correctAnswer(country.getName())
                    .wrongOptions(getWrongCountryNames(country, countries))
                    .difficulty(1)
                    .points(10)
                    .timeLimitSeconds(10)
                    .imageUrl(country.getFlagUrl())
                    .active(true)
                    .build();
            triviaQuestionRepository.save(flagQ);
            questionsGenerated++;

            // Pregunta de moneda
            if (country.getCurrencyName() != null) {
                TriviaQuestion currencyQ = TriviaQuestion.builder()
                        .questionType(QuestionType.CURRENCY)
                        .country(country)
                        .questionText("Cual es la moneda oficial de " + country.getName() + "?")
                        .correctAnswer(country.getCurrencyName())
                        .wrongOptions(getWrongCurrencies(country, countries))
                        .difficulty(2)
                        .points(15)
                        .timeLimitSeconds(15)
                        .explanation("La moneda de " + country.getName() + " es " + country.getCurrencyName() + " (" + country.getCurrencyCode() + ").")
                        .active(true)
                        .build();
                triviaQuestionRepository.save(currencyQ);
                questionsGenerated++;
            }

            // Pregunta de continente
            TriviaQuestion continentQ = TriviaQuestion.builder()
                    .questionType(QuestionType.CONTINENT)
                    .country(country)
                    .questionText("En que continente se encuentra " + country.getName() + "?")
                    .correctAnswer(country.getContinent())
                    .wrongOptions(getWrongContinents(country.getContinent()))
                    .difficulty(1)
                    .points(10)
                    .timeLimitSeconds(10)
                    .active(true)
                    .build();
            triviaQuestionRepository.save(continentQ);
            questionsGenerated++;
        }

        log.info("Se han generado {} preguntas de trivia", questionsGenerated);
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
        List<String> continents = Arrays.asList("Europa", "America", "Asia", "Africa", "Oceania");
        return continents.stream()
                .filter(c -> !c.equals(correct))
                .limit(3)
                .toList();
    }
}
