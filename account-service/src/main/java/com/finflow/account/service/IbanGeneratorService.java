package com.finflow.account.service;

import com.finflow.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Genera IBAN italiani validi e univoci.
 *
 * Formato IBAN italiano: IT + 2 check digits + 1 CIN + 5 ABI + 5 CAB + 12 conto
 * Esempio: IT60 X054 2811 1010 0000 0123 456
 *
 * Per semplicità generiamo un IBAN "plausibile" — in produzione
 * si userebbe un algoritmo MOD97 completo per il calcolo del check digit.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IbanGeneratorService {

    private final AccountRepository accountRepository;

    // Codici fissi per FinFlow (ABI = banca, CAB = filiale)
    private static final String COUNTRY_CODE = "IT";
    private static final String BANK_CODE = "99999";  // ABI FinFlow
    private static final String BRANCH_CODE = "00001"; // CAB
    private static final String CIN = "X";

    private final Random random = new Random();

    /**
     * Genera un IBAN unico — riprova se già esistente nel DB.
     */
    public String generateUniqueIban() {
        String iban;
        int attempts = 0;

        do {
            iban = generateIban();
            attempts++;
            if (attempts > 10) {
                throw new RuntimeException("Unable to generate unique IBAN after 10 attempts");
            }
        } while (accountRepository.existsByIban(iban));

        log.debug("Generated IBAN: {} (attempts: {})", iban, attempts);
        return iban;
    }

    private String generateIban() {
        // Genera 12 cifre casuali per il numero di conto
        String accountNumber = String.format("%012d", (long)(random.nextDouble() * 1_000_000_000_000L));

        // Check digit semplificato (2 cifre random tra 10-99 per sembrare reale)
        int checkDigit = 10 + random.nextInt(89);

        return COUNTRY_CODE + checkDigit + CIN + BANK_CODE + BRANCH_CODE + accountNumber;
    }
}
