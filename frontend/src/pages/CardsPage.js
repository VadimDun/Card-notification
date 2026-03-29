import { useEffect, useState } from "react";

import CardList from "../components/CardList";
import CardForm from "../components/CardForm";

export default function CardsPage() {

    const [cards, setCards] = useState([]);
    const [clients, setClients] = useState([]);

    useEffect(() => {
        loadCards();
        loadClients();
    }, []);

    async function loadCards() {

        fetch("http://localhost:8080/rest/cards")
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

    return (

        <div>

            <h1>Карты</h1>

            <CardForm onCreate={createCard} clients={clients} />

            <CardList cards={cards} onClose={closeCard} onDelete={deleteCard}/>

        </div>

    );

}
