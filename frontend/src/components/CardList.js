export default function CardList({ cards, onClose, onDelete }) {

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
                <th>ID</th>
                <th>Номер</th>
                <th>Дата выдачи</th>
                <th>Дата окончания</th>
                <th>Клиент</th>
                <th>Статус</th>
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
                            <button onClick={() => handleClose(card.id)}>
                                Закрыть
                            </button>
                        )}

                        <button onClick={() => handleDelete(card.id)}>
                            Удалить
                        </button>

                    </td>

                </tr>

            ))}

            </tbody>

        </table>

    );

}
