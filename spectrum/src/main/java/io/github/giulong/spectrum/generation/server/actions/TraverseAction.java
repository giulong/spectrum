package io.github.giulong.spectrum.generation.server.actions;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ToString(callSuper = true)
public class TraverseAction extends NavigateAction {

    @JsonIgnore
    private Status status;

    public int getRelativeIndexFrom(final int index, final List<NavigateAction> navigateActions) {
        final boolean back = urlMatchesWith(index - 1, navigateActions);
        final boolean forward = urlMatchesWith(index + 1, navigateActions);

        this.status = Status.fromBackAndForward(back, forward);

        log.info("Navigation status: {}", status);
        return status.index;
    }

    boolean urlMatchesWith(final int index, final List<NavigateAction> navigateActions) {
        return index >= 0
                && index < navigateActions.size()
                && url.equals(navigateActions.get(index).url);
    }

    @Getter
    @AllArgsConstructor
    public enum Status {

        BACK(-1, true, false),
        FORWARD(1, false, true),
        UNKNOWN(-1, true, true);

        private final int index;
        private final boolean back;
        private final boolean forward;

        public static Status fromBackAndForward(final boolean back, final boolean forward) {
            return Arrays.stream(values())
                    .filter(v -> v.back == back)
                    .filter(v -> v.forward == forward)
                    .findFirst()
                    .orElse(UNKNOWN);
        }
    }
}
