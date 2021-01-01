package ua.kyivstar.cas.ticket.registry;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Builder
public class AccountTgtBox implements Serializable {
    private static final long serialVersionUID = -1695946440519004960L;

    @Getter
    private String accountId;
    @Getter
    @Builder.Default
    private List<String> tgtList = new ArrayList<>();
}
