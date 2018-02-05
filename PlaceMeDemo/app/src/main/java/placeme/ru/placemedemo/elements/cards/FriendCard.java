package placeme.ru.placemedemo.elements.cards;

/**
 * Class that describes information that should be displayed
 * in the user profile in card of friend
 * Created by Андрей on 20.12.2017.
 */
public class FriendCard {
    private String mCardName;
    private int mImageResourceId;
    private int id;

    /**
     * Method that returns card name
     * @return card name
     */
    public String getCardName() {
        return mCardName;
    }

    /**
     * Method that changes current card name
     * @param mCardName new card name
     */
    public void setCardName(String mCardName) {
        this.mCardName = mCardName;
    }

    /**
     * Method that changes card id
     * @param id new card id
     */
    public void setId(int id) { this.id = id; }

    /**
     * Method that returns current card id
     * @return card id
     */
    public int getId() { return id; }

    /**
     * Method that returns current image id of card
     * @return card image id
     */
    public int getImageResourceId() {
        return mImageResourceId;
    }

    /**
     * Method that changes image resource id of a card
     * @param mImageResourceId new image resource id
     */
    public void setImageResourceId(int mImageResourceId) {
        this.mImageResourceId = mImageResourceId;
    }
}
