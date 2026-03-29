import { useEffect, useState } from "react";

import ClientList from "../components/ClientList";
import ClientForm from "../components/ClientForm";

export default function ClientsPage() {

    const [clients, setClients] = useState([]);

    useEffect(() => {
        loadClients();
    }, []);

    async function loadClients() {

        await fetch("http://localhost:8080/rest/clients")
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

            <ClientForm onCreate={createClient} />

            <ClientList clients={clients} onDelete={deleteClient}/>

        </div>

    );
}
