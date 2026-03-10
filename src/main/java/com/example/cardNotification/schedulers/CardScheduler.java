package com.example.cardNotification.schedulers;

import com.example.cardNotification.models.Card;
import com.example.cardNotification.notifiers.CardNotifier;
import com.example.cardNotification.services.CardService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class CardScheduler {
    private final CardService cardService;
    private final CardNotifier cardNotifier;

    public CardScheduler(CardService cardService, CardNotifier cardNotifier) {
        this.cardService = cardService;
        this.cardNotifier = cardNotifier;
    }

    @Scheduled(fixedRate = 10000) // for testing
//    @Scheduled(cron = "0 0 0 * * *")
    public void checkExpiringCardsForNotification() {
        List<Card> expiringCards = cardService.getExpiredCardsAndNotNotified();

//        cardNotifier.notifyClient(LocalDate.now().toString() + "\n");

        for (Card card : expiringCards) {
            String message = String.format(
                    "%s, срок действия вашей карты номер %s истек %s\n",
                    card.getClient().getFullName(),
                    card.getCardNumber(),
                    card.getExpDate()
            );
            cardNotifier.notifyClient(message);
            card.setNotified(true);
        }
    }
}
