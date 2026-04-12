package io.github.giulong.spectrum.generation.server.actions;

import static io.github.giulong.spectrum.generation.server.actions.TraverseAction.Status.BACK;
import static io.github.giulong.spectrum.generation.server.actions.TraverseAction.Status.FORWARD;
import static io.github.giulong.spectrum.generation.server.actions.TraverseAction.Status.UNKNOWN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import io.github.giulong.spectrum.utils.Reflections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class TraverseActionTest {

    private final String url = "url";
    private final String nope = "nope";
    private final int navigateIndex = 123;
    private final int size = 999;

    @Mock
    private NavigateAction previousNavigateAction;

    @Mock
    private NavigateAction nextNavigateAction;

    @Mock
    private List<NavigateAction> navigateActions;

    @InjectMocks
    private TraverseAction action;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("url", action, url);

        when(navigateActions.size()).thenReturn(size);
    }

    @Test
    @DisplayName("getStatusFrom should return BACK when the url equals to the previous one")
    void getRelativeIndexFromBack() {
        when(navigateActions.get(navigateIndex - 1)).thenReturn(previousNavigateAction);
        when(navigateActions.get(navigateIndex + 1)).thenReturn(nextNavigateAction);

        Reflections.setField("url", previousNavigateAction, url);
        Reflections.setField("url", nextNavigateAction, nope);

        assertEquals(BACK.getIndex(), action.getRelativeIndexFrom(navigateIndex, navigateActions));
    }

    @Test
    @DisplayName("getStatusFrom should return FORWARD when the url equals to the next one")
    void getRelativeIndexFromForward() {
        when(navigateActions.get(navigateIndex - 1)).thenReturn(previousNavigateAction);
        when(navigateActions.get(navigateIndex + 1)).thenReturn(nextNavigateAction);

        Reflections.setField("url", previousNavigateAction, nope);
        Reflections.setField("url", nextNavigateAction, url);

        assertEquals(FORWARD.getIndex(), action.getRelativeIndexFrom(navigateIndex, navigateActions));
    }

    @Test
    @DisplayName("getStatusFrom should return UNKNOWN when the url equals both to the previous and to the next one")
    void getRelativeIndexFromUnknown() {
        when(navigateActions.get(navigateIndex - 1)).thenReturn(previousNavigateAction);
        when(navigateActions.get(navigateIndex + 1)).thenReturn(nextNavigateAction);

        Reflections.setField("url", previousNavigateAction, url);
        Reflections.setField("url", nextNavigateAction, url);

        assertEquals(UNKNOWN.getIndex(), action.getRelativeIndexFrom(navigateIndex, navigateActions));
    }

    @Test
    @DisplayName("getStatusFrom should skip the BACK check if the current navigation index is 0")
    void getRelativeIndexFromForwardNavigateIndexZero() {
        when(navigateActions.get(1)).thenReturn(nextNavigateAction);

        Reflections.setField("url", nextNavigateAction, url);

        assertEquals(FORWARD.getIndex(), action.getRelativeIndexFrom(0, navigateActions));

        verify(previousNavigateAction, never()).getUrl();
    }

    @Test
    @DisplayName("getStatusFrom should skip the FORWARD check if the current navigation index is greater than the all navigation size")
    void getRelativeIndexFromBackNavigateIndexZero() {
        when(navigateActions.get(size - 1)).thenReturn(previousNavigateAction);

        Reflections.setField("url", previousNavigateAction, url);

        assertEquals(BACK.getIndex(), action.getRelativeIndexFrom(size, navigateActions));

        verify(nextNavigateAction, never()).getUrl();
    }

}
