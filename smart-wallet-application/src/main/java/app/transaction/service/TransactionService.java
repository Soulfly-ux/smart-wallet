package app.transaction.service;

import app.exceptions.DomainException;
import app.transaction.model.Transaction;
import app.transaction.model.TransactionStatus;
import app.transaction.model.TransactionType;
import app.transaction.repository.TransactionRepository;
import app.user.model.User;
import app.wallet.model.Wallet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(User owner, String sender, String receiver, BigDecimal transactionAmount, BigDecimal balanceLeft, Currency currency, TransactionType type, TransactionStatus status, String transactionDescription, String failureReason) {
        Transaction transaction = Transaction.builder()
                .owner(owner)
                .sender(sender)
                .receiver(receiver)
                .amount(transactionAmount)
                .balanceLeft(balanceLeft)
                .currency(currency)
                .transactionType(type)
                .transactionStatus(status)
                .description(transactionDescription)
                .failureReason(failureReason)
                .createdOn(LocalDateTime.now())
                .build();

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllByOwnerId(UUID ownerId) {
      return   transactionRepository.findAllByOwnerIdOrderByCreatedOnDesc(ownerId);
    }

    public Transaction getTransactionById(UUID id) {

        return transactionRepository.findById(id).orElseThrow(() -> new DomainException("Transaction not found"));
    }

    public List<Transaction> getLastFourTransactionsByWalletId(Wallet wallet) {
        // при една операция(Пешо праща 100 лв. на Мария) имаме винаги 2 транзакции- веднъл взимане на пари(WITHDRAW) и веднъж даване на пари(DEPOSIT)
        // взимането на пари тази транзакция пренадлежи на Пешо, защото от него са взети пари , а даването на пари тази транзакция пренадлежи на Мария, защото е на нея, която се дава
        // МНОГО ВАЖНО Е КОГАТО ВЗИМАМЕ ПОСЛЕДНИТЕ 4 ТРАНЗАКЦИИ, ДА ВЗЕМЕМ ТЕЗИ КОИТО СА САМО СОБСТВЕНОСТ НА ДАДЕНИЯ ПОРТФЕЙЛ, ЗАЩОТО ТОЙ МОЖЕ ДА Е УЧАСТВАЛ И В ТРАНЗАКЦИИ
        // , КОИТО НЕ СА НА  ТОЗИ ПОРТФЕЙЛ. С примера горе-> за портфейла на Пешо взимаме само трнзакцията в която са взети пари от неговия портфейл.


        List<Transaction> lastTransactions = transactionRepository.findAllBySenderOrReceiverOrderByCreatedOnDesc(wallet.getId().toString(), wallet.getId().toString()).stream()
                .filter(transaction -> transaction.getOwner().getId() == wallet.getOwner().getId())// искам и транзакцията и портфейла да са на един собственик, така искам да взема тарнзакциите на ПЕШО
                .filter(transaction -> transaction.getTransactionStatus() == TransactionStatus.SUCCEEDED)
                .limit(4)
                .toList();
        return lastTransactions;
    }

}
