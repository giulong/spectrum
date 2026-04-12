package io.github.giulong.spectrum.generation.server.actions;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;

@Getter
@SuppressWarnings("unused")
@JsonTypeInfo(use = NAME, include = EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = NavigateAction.class, name = "navigate"),
        @JsonSubTypes.Type(value = ElementNavigateAction.class, name = "elementNavigate"),
        @JsonSubTypes.Type(value = TraverseAction.class, name = "traverse"),
        @JsonSubTypes.Type(value = ClickAction.class, name = "click"),
        @JsonSubTypes.Type(value = InputAction.class, name = "input"),
})
public abstract class Action {
    private String type;
}
