package placeme.ru.placemedemo.core.utils;

import com.google.android.gms.location.places.Place;

import java.util.List;

/**
 * Class that converts google places to application place
 * Created by Андрей on 02.02.2018.
 */

public class ConverterToPlace {
    /**
     * Method that converts google place to place with application structure
     * @param place place to convert
     * @return converted place
     */
    public static placeme.ru.placemedemo.elements.Place convertGooglePlaceToPlace(Place place) {
        placeme.ru.placemedemo.elements.Place result = new placeme.ru.placemedemo.elements.Place();
        String name = "Some place";
        String address = "";
        String phone = "";
        if (place.getName() != null) {
            name = place.getName().toString();
        }

        if (place.getAddress() != null) {
            address = place.getAddress().toString();
        }

        if (place.getPhoneNumber() != null) {
            phone = place.getPhoneNumber().toString();
        }

        StringBuilder description = new StringBuilder();
        description.append(name);

        if (!address.equals("")) {
            description.append('\n');
            description.append(address);
        }

        if (!phone.equals("")) {
            description.append('\n');
            description.append(phone);
        }

        result.setName(place.getName().toString());
        result.setDescription(description.toString());

        result = addTags(result, place);

        result.setLatitude(place.getLatLng().latitude);

        result.setLongitude(place.getLatLng().longitude);
        return result;
    }

    private static placeme.ru.placemedemo.elements.Place addTags(placeme.ru.placemedemo.elements.Place destination, Place src) {
        List<Integer> placeTags = src.getPlaceTypes();
        StringBuilder tags = new StringBuilder();

        for (Integer id : placeTags) {
            if (id.equals(Place.TYPE_AIRPORT)) {
                tags.append("аэропорт,");
                tags.append("самолёт,");
            } else if (id.equals(Place.TYPE_ART_GALLERY)) {
                tags.append("галерея,");
                tags.append("арт,");
                tags.append("искусство,");
                tags.append("культура,");
                tags.append("картины,");
            } else if (id.equals(Place.TYPE_ATM)) {
                tags.append("банк,");
                tags.append("банкомат,");
                tags.append("деньги,");
            } else if (id.equals(Place.TYPE_BAKERY)) {
                tags.append("пекарня,");
                tags.append("выпечка,");
                tags.append("хлеб,");
                tags.append("булочная,");
            } else if (id.equals(Place.TYPE_BANK)) {
                tags.append("банк,");
                tags.append("деньги,");
            } else if (id.equals(Place.TYPE_BAR)) {
                tags.append("бар,");
                tags.append("кафе,");
                tags.append("досуг,");
            } else if (id.equals(Place.TYPE_BEAUTY_SALON)) {
                tags.append("салон красоты,");
                tags.append("красота,");
                tags.append("спа,");
                tags.append("салон,");
                tags.append("парикмахерская,");
            } else if (id.equals(Place.TYPE_BICYCLE_STORE)) {
                tags.append("велосипед,");
                tags.append("магазин,");
            } else if (id.equals(Place.TYPE_BOOK_STORE)) {
                tags.append("книги,");
                tags.append("магазин книг,");
                tags.append("магазин,");
                tags.append("канцелярия,");
            } else if (id.equals(Place.TYPE_BUS_STATION)) {
                tags.append("автобус,");
                tags.append("остановка,");
                tags.append("троллейбус,");
                tags.append("транспорт,");
            } else if (id.equals(Place.TYPE_CAFE)) {
                tags.append("кафе,");
                tags.append("еда,");
                tags.append("ресторан,");
            } else if (id.equals(Place.TYPE_CAR_REPAIR)) {
                tags.append("машина,");
                tags.append("ремонт,");
                tags.append("ремонт машин,");
                tags.append("запчасти,");
            } else if (id.equals(Place.TYPE_CAR_WASH)) {
                tags.append("машина,");
                tags.append("мойка,");
                tags.append("мойка машин,");
            } else if (id.equals(Place.TYPE_CHURCH)) {
                tags.append("церковь,");
            } else if (id.equals(Place.TYPE_CLOTHING_STORE)) {
                tags.append("магазин,");
                tags.append("одежда,");
                tags.append("магазин одежды,");
            } else if (id.equals(Place.TYPE_COUNTRY)) {
                tags.append("страна,");
                tags.append("государство,");
            } else if (id.equals(Place.TYPE_DENTIST)) {
                tags.append("дантист,");
                tags.append("стоматология,");
                tags.append("клиника,");
                tags.append("стоматолог,");
            } else if (id.equals(Place.TYPE_DEPARTMENT_STORE)) {
                tags.append("универмаг,");
            } else if (id.equals(Place.TYPE_DOCTOR)) {
                tags.append("доктор,");
                tags.append("врач,");
                tags.append("больница,");
                tags.append("поликлиника,");
            } else if (id.equals(Place.TYPE_EMBASSY)) {
                tags.append("консул,");
                tags.append("консульство,");
                tags.append("посольство,");
                tags.append("посол,");
            } else if (id.equals(Place.TYPE_FIRE_STATION)) {
                tags.append("пожарная,");
                tags.append("пожарная станция,");
                tags.append("пожарная часть,");
            } else if (id.equals(Place.TYPE_FLORIST)) {
                tags.append("цветы,");
                tags.append("цветочный магазин,");
                tags.append("магазин цветов,");
                tags.append("флорист,");
            } else if (id.equals(Place.TYPE_FLORIST)) {
                tags.append("цветы,");
                tags.append("цветочный магазин,");
                tags.append("магазин цветов,");
                tags.append("флорист,");
            } else if (id.equals(Place.TYPE_FOOD)) {
                tags.append("еда,");
            } else if (id.equals(Place.TYPE_GROCERY_OR_SUPERMARKET)) {
                tags.append("магазин,");
                tags.append("супермаркет,");
                tags.append("еда,");
            } else if (id.equals(Place.TYPE_GYM)) {
                tags.append("фитнес,");
                tags.append("спорт,");
                tags.append("спортзал,");
                tags.append("тренажёры,");
            } else if (id.equals(Place.TYPE_HAIR_CARE)) {
                tags.append("парикмахерская,");
                tags.append("волосы,");
                tags.append("стрижка,");
                tags.append("парикмахер,");
            } else if (id.equals(Place.TYPE_HEALTH)) {
                tags.append("здоровье,");
            } else if (id.equals(Place.TYPE_LIBRARY)) {
                tags.append("книги,");
                tags.append("библиотека,");
                tags.append("чтение,");
                tags.append("досуг,");
            } else if (id.equals(Place.TYPE_MOVIE_THEATER)) {
                tags.append("кино,");
                tags.append("кинотеатр,");
                tags.append("фильмы,");
                tags.append("досуг,");
            } else if (id.equals(Place.TYPE_MUSEUM)) {
                tags.append("музей,");
                tags.append("культура,");
                tags.append("образование,");
            } else if (id.equals(Place.TYPE_PARK)) {
                tags.append("парк,");
                tags.append("прогулка,");
                tags.append("сад,");
                tags.append("деревья,");
            } else if (id.equals(Place.TYPE_PARKING)) {
                tags.append("парковка,");
                tags.append("стоянка,");
            } else if (id.equals(Place.TYPE_PET_STORE)) {
                tags.append("зоомагазин,");
                tags.append("магазин,");
                tags.append("питомец,");
            } else if (id.equals(Place.TYPE_PHARMACY)) {
                tags.append("аптека,");
                tags.append("лекарства,");
            } else if (id.equals(Place.TYPE_POLICE)) {
                tags.append("полиция,");
                tags.append("полицеский участок,");
            } else if (id.equals(Place.TYPE_POST_OFFICE)) {
                tags.append("почта,");
                tags.append("посылка,");
                tags.append("письмо,");
            } else if (id.equals(Place.TYPE_RESTAURANT)) {
                tags.append("ресторан,");
                tags.append("кафе,");
                tags.append("еда,");
                tags.append("уют,");
            } else if (id.equals(Place.TYPE_SCHOOL)) {
                tags.append("школа,");
                tags.append("образование,");
            } else if (id.equals(Place.TYPE_SHOE_STORE)) {
                tags.append("обувь,");
                tags.append("магазин,");
                tags.append("магазин обуви,");
            } else if (id.equals(Place.TYPE_SHOPPING_MALL)) {
                tags.append("торговый центр,");
                tags.append("магазин,");
                tags.append("тц,");
                tags.append("шопинг,");
            } else if (id.equals(Place.TYPE_SPA)) {
                tags.append("спа,");
                tags.append("отдых,");
                tags.append("красота,");
            } else if (id.equals(Place.TYPE_STADIUM)) {
                tags.append("спорт,");
                tags.append("стадион,");
            } else if (id.equals(Place.TYPE_STORE)) {
                tags.append("магазин,");
            } else if (id.equals(Place.TYPE_STREET_ADDRESS)) {
                tags.append("улица,");
            } else if (id.equals(Place.TYPE_SUBWAY_STATION)) {
                tags.append("метро,");
                tags.append("станция метро,");
                tags.append("транспорт,");
            } else if (id.equals(Place.TYPE_TRAIN_STATION)) {
                tags.append("поезд,");
                tags.append("транспорт,");
                tags.append("вокзал,");
            } else if (id.equals(Place.TYPE_UNIVERSITY)) {
                tags.append("университет,");
                tags.append("обучение,");
                tags.append("образование,");
                tags.append("институт,");
                tags.append("вуз,");
            } else if (id.equals(Place.TYPE_VETERINARY_CARE)) {
                tags.append("ветеринар,");
                tags.append("питомец,");
            } else if (id.equals(Place.TYPE_ZOO)) {
                tags.append("зоопарк,");
                tags.append("животные,");
                tags.append("досуг,");
            }
        }

        if (tags.length() > 0) {
            tags.deleteCharAt(tags.lastIndexOf(","));
        }

        destination.setTags(tags.toString());

        return destination;
    }
}
