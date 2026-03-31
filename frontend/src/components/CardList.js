export default function CardList({ cards, onClose, onDelete, onSort }) {

    function handleDelete(id) {

        if (window.confirm("Удалить карту?")) {
            onDelete(id);
        }

    }

    function handleClose(id) {

        if (window.confirm("Закрыть карту?")) {
            onClose(id);
        }

    }

    function formatDate(date) {

        return new Date(date)
            .toLocaleDateString();

    }

    function getStatus(active) {

        return active ? "Открыта" : "Закрыта";

    }

    function getStatusClass(active) {

        return active
            ? "status-open"
            : "status-closed";

    }

    return (

        <table>

            <thead>
            <tr>
                <th onClick={() => onSort("id")}>
                    ID
                </th>
                <th onClick={() => onSort("cardNumber")}>
                    Номер
                </th>
                <th onClick={() => onSort("issueDate")}>
                    Дата выдачи
                </th>
                <th onClick={() => onSort("expDate")}>
                    Дата окончания
                </th>

                <th onClick={() => onSort("clientName")}>
                    Клиент
                </th>
                <th onClick={() => onSort("active")}>
                    Статус
                </th>
                <th>Действия</th>
            </tr>
            </thead>

            <tbody>

            {cards.map(card => (

                <tr key={card.id}>

                    <td>{card.id}</td>

                    <td>{card.cardNumber}</td>

                    <td>{formatDate(card.issueDate)}</td>

                    <td>{formatDate(card.expDate)}</td>

                    <td>{card.clientName}</td>

                    <td className={getStatusClass(card.active)}>
                        {getStatus(card.active)}
                    </td>

                    <td>

                        {card.active && (
                            <button onClick={() => handleClose(card.id)}  className="btn-close">
                                Закрыть
                            </button>
                        )}

                        {!card.active && (
                            <button onClick={() => handleDelete(card.id)} className="btn-delete">
                                Удалить
                            </button>
                        )}

                    </td>

                </tr>

            ))}

            </tbody>

        </table>

    );

}
