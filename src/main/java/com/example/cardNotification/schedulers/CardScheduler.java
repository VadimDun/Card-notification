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

    private final boolean sendMessage = false; // for testing

    public CardScheduler(CardService cardService, CardNotifier cardNotifier) {
        this.cardService = cardService;
        this.cardNotifier = cardNotifier;
    }

    @Scheduled(fixedRate = 30000) // for testing
//    @Scheduled(cron = "0 0 0 * * *")
    // todo проверка на отправку через 1/2 недели(?)
    public void checkExpiringCardsForNotification() {
        checkExpired();
        checkWeekBeforeExpired();
        check2WeekBeforeExpired();
    }

    private void checkExpired(){
        List<Card> expiringCards = cardService.getExpiredCardsAndNotNotified();
        for (Card card : expiringCards) {
            Card newCard = cardService.reissueCard(card);
            String message = String.format(
                    "%s, срок действия вашей карты номер %s истек %s." +
                            "\nОткрыта новая карта. Подробности:" +
                            "\nНомер карты: %s \nСрок действия: %s\n",
                    card.getClient().getFullName(),
                    card.getCardNumber(),
                    card.getExpDate(),
                    newCard.getCardNumber(),
                    newCard.getExpDate()
            );
            cardNotifier.notifyClient(message);
            if (sendMessage) {
                cardNotifier.notifyByEmail(
                        card.getClient().getEmail(),
                        "Срок действия вашей карты истек. Мы открыли новую карту",
                        message
                );
            }
            card.setNotified(true);
            cardService.saveCard(card);
        }
    }

    private void checkWeekBeforeExpired(){
        LocalDate week = LocalDate.now().plusWeeks(1);
        List<Card> weekCards = cardService.getCardsExpiringOn(week);
        for (Card card : weekCards) {
            String message = String.format(
                    "%s, срок действия вашей карты номер %s истекает через неделю %s\n",
                    card.getClient().getFullName(),
                    card.getCardNumber(),
                    week
            );
            cardNotifier.notifyClient(message);
            if (sendMessage) {
                cardNotifier.notifyByEmail(
                        card.getClient().getEmail(),
                        "Срок действия вашей карты истекает через неделю",
                        message
                );
            }
        }
    }

    private void check2WeekBeforeExpired(){
        LocalDate week2 = LocalDate.now().plusWeeks(2);
        List<Card> week2Cards = cardService.getCardsExpiringOn(week2);
        for (Card card : week2Cards) {
            String message = String.format(
                    "%s, срок действия вашей карты номер %s истекает через 2 недели %s\n",
                    card.getClient().getFullName(),
                    card.getCardNumber(),
                    week2
            );
            cardNotifier.notifyClient(message);
            if (sendMessage) {
                cardNotifier.notifyByEmail(
                        card.getClient().getEmail(),
                        "Срок действия вашей карты истекает через 2 недели",
                        message
                );
            }
        }
    }
}
