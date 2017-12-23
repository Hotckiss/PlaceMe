package placeme.ru.placemedemo.elements.cards;

/**
 * Created by Андрей on 20.12.2017.
 */

/**
 * Class that describes information that should be displayed
 * in the user profile in card of friend
 */
public class FriendCard {
    private String cardName;
    private int imageResourceId;
    private int id;

    /**
     * Method that returns card name
     * @return card name
     */
    public String getCardName() {
        return cardName;
    }

    /**
     * Method that changes current card name
     * @param cardName new card name
     */
    public void setCardName(String cardName) {
        this.cardName = cardName;
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
        return imageResourceId;
    }

    /**
     * Method that changes image resource id of a card
     * @param imageResourceId new image resource id
     */
    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}
