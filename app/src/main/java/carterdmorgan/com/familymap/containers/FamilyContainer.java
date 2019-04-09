package carterdmorgan.com.familymap.containers;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import carterdmorgan.com.familymap.api.model.Person;

public class FamilyContainer {
    private Person person;
    private String relationship;

    public FamilyContainer(Person person, String relationship) {
        this.person = person;
        this.relationship = relationship;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public static RecyclerView.LayoutManager getNoScrollManager(Context context) {
        return new LinearLayoutManager(context) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
    }

    @Override
    public String toString() {
        return "FamilyContainer{" +
                "person=" + person +
                ", relationship='" + relationship + '\'' +
                '}';
    }
}
