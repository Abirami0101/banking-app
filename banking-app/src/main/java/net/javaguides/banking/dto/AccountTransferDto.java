package net.javaguides.banking.dto;

public record AccountTransferDto(Long fromAccountId,
                                 Long toAccountId,
                                 double amount) {
}
