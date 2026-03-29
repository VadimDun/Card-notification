export default function CardList({ cards, onClose, onDelete }) {

    return (

        <ul>

            {cards.map(card => (

                <li key={card.id}>

                    ID: {card.id}
                    {" | "}
                    Номер: {card.cardNumber}
                    {", "}
                    Выпущена: {card.issueDate}
                    {", "}
                    Срок действия: {card.expDate}
                    {" "}
                    (ID клиента: {card.clientId})

                    <button onClick={() => onClose(card.id)}>
                        Закрыть
                    </button>

                    <button onClick={() => onDelete(card.id)}>
                        Удалить
                    </button>

                </li>

            ))}

        </ul>

    );

}
