export default function ClientList({ clients, onDelete }) {

    return (

        <ul>
            {clients.map(client => (
                <li key={client.id}>
                    ID: {client.id}
                    {" | "}
                    {client.fullName}({client.birthDate}): {client.email}
                    <button onClick={() => onDelete(client.id)}>
                        Удалить
                    </button>
                </li>
            ))}
        </ul>

    );
}
