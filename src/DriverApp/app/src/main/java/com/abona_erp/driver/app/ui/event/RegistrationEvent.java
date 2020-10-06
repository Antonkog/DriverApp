package com.abona_erp.driver.app.ui.event;

public class RegistrationEvent implements BaseEvent {
    public enum State{STARTED,FINISHED,ERROR}
    State state;

    public RegistrationEvent(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }
}
