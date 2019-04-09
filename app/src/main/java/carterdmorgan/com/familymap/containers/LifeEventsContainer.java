package carterdmorgan.com.familymap.containers;

import java.util.Comparator;

import carterdmorgan.com.familymap.api.model.Event;

public class LifeEventsContainer {
    private Event event;
    private String personName;

    public LifeEventsContainer(Event event, String personName) {
        this.event = event;
        this.personName = personName;
    }

    public static Comparator<LifeEventsContainer> SORT_BY_YEAR_AND_NAME =
        new Comparator<LifeEventsContainer>() {
            @Override
            public int compare(LifeEventsContainer o1, LifeEventsContainer o2) {
                Integer i1 = new Integer(o1.getEvent().getYear());
                Integer i2 = new Integer(o2.getEvent().getYear());

                if (!i2.equals(i1)) {
                    return i1.compareTo(i2);
                } else {
                    return o1.getEvent().getEventType().toLowerCase()
                            .compareTo(o2.getEvent().getEventType().toLowerCase());
                }
            }
        };

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    @Override
    public String toString() {
        return "LifeEventsContainer{" +
                "event=" + event +
                ", personName='" + personName + '\'' +
                '}';
    }
}
