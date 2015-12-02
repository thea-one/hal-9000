package one.thea.nightynight;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JA on 2015-12-02.
 */
public class LullabyShuffle {

    private List<Integer> lullabies = new ArrayList<>();
    public LullabyShuffle()
    {
        this.populateLullabies();
    }
    public int getLullabyID() {
        int range     = this.lullabies.size();
        int randomInt = (int)(Math.random() * range) + 1;
        return this.getLullabies().get(randomInt-1);
    }
    private void populateLullabies()
    {
        this.getLullabies().add(R.raw.lullaby);
    }
    private List<Integer> getLullabies() {
        return lullabies;
    }

    private void setLullabies(List<Integer> lullabies) {
        this.lullabies = lullabies;
    }

}
