import { useEffect, useState } from "react";

export default function App() {

    const [clients, setClients] = useState([]);

    const [fullName, setFullName] = useState("");
    const [birthDate, setBirthDate] = useState("");
    const [email, setEmail] = useState("");

    useEffect(() => {
        loadClients();
    }, []);

    function loadClients() {

        fetch("http://localhost:8080/rest/clients")
            .then(response => response.json())
            .then(data => setClients(data));

    }

    function createClient(e) {

        e.preventDefault();

        fetch("http://localhost:8080/rest/clients", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                fullName: fullName,
                birthDate: birthDate,
                email: email
            })
        })
            .then(() => {
                loadClients();
            });

    }

    return (
        <div>

            <h1>Все клиенты:</h1>

            <form onSubmit={createClient}>

                <input
                    placeholder="Полное имя клиента"
                    value={fullName}
                    onChange={e => setFullName(e.target.value)}
                />

                <input
                    type="Date"
                    value={birthDate}
                    onChange={e => setBirthDate(e.target.value)}
                />

                <input
                    placeholder="Email"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                />

                <button type="submit">
                    Добавить клиента
                </button>

            </form>

            <ul>
                {clients.map(client => (
                    <li key={client.id}>
                        {client.fullName}({client.birthDate}): {client.email}
                    </li>
                ))}
            </ul>

        </div>
    );
}