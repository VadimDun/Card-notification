import { useEffect, useState } from "react";

import CardList from "../components/CardList";
import CardForm from "../components/CardForm";

export default function CardsPage() {

    const [cards, setCards] = useState([]);
    const [clients, setClients] = useState([]);

    const [selectedClientId, setSelectedClientId] = useState("");

    const [sortField, setSortField] = useState("id");
    const [sortDirection, setSortDirection] = useState("asc");

    const processedCards = sortCards(cards);

    useEffect(() => {
        loadCards(selectedClientId);
        loadClients();
    }, [selectedClientId]);

    async function loadCards(clientId = "") {

        let url = "http://localhost:8080/rest/cards";

        if (clientId) {
            url += `?clientId=${clientId}`;
        }

        fetch(url)
            .then(response => response.json())
            .then(data => setCards(data));

    }

    async function loadClients() {
        fetch("http://localhost:8080/rest/clients")
            .then(response => response.json())
            .then(data => setClients(data));
    }

    async function createCard(card) {
        await fetch("http://localhost:8080/rest/cards", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(card)
        })
            .then(() => {
                loadCards();
            });
    }

    async function closeCard(id) {
        await fetch(`http://localhost:8080/rest/cards/close/${id}`, {
            method: "POST"
        })
            .then(() => {
                loadCards();
            });
    }

    async function deleteCard(id) {
        await fetch(`http://localhost:8080/rest/cards/${id}`, {
            method: "DELETE"
        })
            .then(() => {
                alert("Карта создана");
                loadCards();
            });
    }

    function sortCards(cards) {

        return [...cards].sort((a, b) => {

            let valueA = a[sortField];
            let valueB = b[sortField];

            if (sortField.includes("Date")) {
                valueA = new Date(valueA);
                valueB = new Date(valueB);
            }

            if (valueA < valueB)
                return sortDirection === "asc" ? -1 : 1;

            if (valueA > valueB)
                return sortDirection === "asc" ? 1 : -1;

            return 0;

        });

    }

    function handleSort(field) {

        if (field === sortField) {
            setSortDirection(
                sortDirection === "asc" ? "desc" : "asc"
            );

        } else {
            setSortField(field);
            setSortDirection("asc");

        }

    }

    return (

        <div>

            <h1>Карты</h1>

            <CardForm onCreate={createCard} clients={clients}/>

            <select value={selectedClientId}
                onChange={e =>
                    setSelectedClientId(e.target.value)
                }
            >

                <option value="">
                    Все клиенты
                </option>

                {clients.map(client => (

                    <option key={client.id} value={client.id}>
                        {client.fullName}
                    </option>

                ))}

            </select>

            <CardList cards={processedCards} onClose={closeCard} onDelete={deleteCard} onSort={handleSort}/>

        </div>

    );

}
