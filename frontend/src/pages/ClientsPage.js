import { useEffect, useState } from "react";

import ClientList from "../components/ClientList";
import ClientForm from "../components/ClientForm";

export default function ClientsPage() {

    const [clients, setClients] = useState([]);

    const [searchName, setSearchName] = useState("");

    useEffect(() => {
        loadClients();
    }, [searchName]);

    async function loadClients() {
        let url = "http://localhost:8080/rest/clients";

        if (searchName) {
            url += `?name=${searchName}`;
        }

        fetch(url)
            .then(response => response.json())
            .then(data => setClients(data));

    }

    async function createClient(client) {
        await fetch("http://localhost:8080/rest/clients", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(client)
        })
            .then(() => {
                alert("Клиент создан");
                loadClients();
            });
    }

    async function deleteClient(id) {

        await fetch(`http://localhost:8080/rest/clients/${id}`, {
            method: "DELETE"
        })
            .then(() => {
                loadClients();
            });
    }

    return (

        <div>

            <h1>Клиенты</h1>

            <ClientForm onCreate={createClient}/>

            <input
                placeholder="Поиск клиента по имени"
                value={searchName}
                onChange={e =>
                    setSearchName(e.target.value)
                }
            />

            <ClientList clients={clients} onDelete={deleteClient}/>

        </div>

    );
}
