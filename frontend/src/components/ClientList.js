export default function ClientList({ clients, onDelete }) {

    function handleDelete(id) {

        const confirmed = window.confirm(
            "Удалить клиента?"
        );

        if (confirmed) {
            onDelete(id);
        }

    }

    return (

        <table>

            <thead>
            <tr>
                <th>ID</th>
                <th>Имя</th>
                <th>Дата рождения</th>
                <th>Email</th>
                <th>Действия</th>
            </tr>
            </thead>

            <tbody>

            {clients.map(client => (

                <tr key={client.id}>

                    <td>{client.id}</td>
                    <td>{client.fullName}</td>
                    <td>{client.birthDate}</td>
                    <td>{client.email}</td>

                    <td>

                        <button onClick={() => handleDelete(client.id)}>
                            Удалить
                        </button>

                    </td>

                </tr>

            ))}

            </tbody>

        </table>

    );
}
