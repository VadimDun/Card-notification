import { useEffect, useState } from "react";

import CardList from "../components/CardList";
import CardForm from "../components/CardForm";

export default function CardsPage() {

    const [cards, setCards] = useState([]);
    const [clients, setClients] = useState([]);

    const [selectedClientId, setSelectedClientId] = useState("");

    const [sortField, setSortField] = useState("id");
    const [sortDirection, setSortDirection] = useState("asc");

    const [searchNumber, setSearchNumber] = useState("");

    const processedCards = sortCards(cards);

    useEffect(() => {
        loadCards(selectedClientId);
        loadClients();
    }, [selectedClientId, searchNumber]);

    async function loadCards(clientId = "") {

        let url = "http://localhost:8080/rest/cards";

        if (clientId && searchNumber) {
            url += `?clientId=${clientId}&number=${searchNumber}`;
        }
        else if (clientId) {
            url += `?clientId=${clientId}`;
        }
        else if (searchNumber) {
            url += `?number=${searchNumber}`;
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
        const response = await fetch("http://localhost:8080/rest/cards", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(card)
        })
        if (response.status === 201){
            loadCards()
        }
        else if (response.status === 404){
            alert("Клиент с таким id не существует")
        }
        else {
            alert("Произошла ошибка при создании карты");
        }
    }

    async function issueCard(clientId) {

        const response = await fetch(
            `http://localhost:8080/rest/cards/issue/${clientId}`,
            {
                method: "POST"
            }
        );

        if (response.status === 201) {
            await loadCards(clientId);
            setSelectedClientId(clientId);
        } else if (response.status === 404) {
            alert("Клиент не найден");
        } else {
            alert("Произошла ошибка при выпуске карты");
        }
    }

    async function closeCard(id) {
        const response = await fetch(`http://localhost:8080/rest/cards/close/${id}`, {
            method: "POST"
        });

        if (response.status === 204) {
            loadCards(selectedClientId);
        } else if (response.status === 404) {
            alert("Карта не найдена");
        } else if (response.status === 409) {
            alert("Карта уже закрыта");
        } else {
            alert("Произошла ошибка при закрытии карты");
        }
    }

    async function deleteCard(id) {
        const response = await fetch(`http://localhost:8080/rest/cards/${id}`, {
            method: "DELETE"
        });

        if (response.status === 204) {
            loadCards(selectedClientId);
        } else if (response.status === 404) {
            alert("Клиент с таким ID не найден");
        } else if (response.status === 409) {
            alert(`Нельзя удалить активную карту`);
        } else {
            alert("Произошла ошибка при удалении");
        }
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

            <button disabled={!selectedClientId}
                onClick={() =>
                    issueCard(selectedClientId)
                }
                className="btn-issue"
            >
                Выпустить карту выбранному клиенту
            </button>

            <br/>

            <input
                placeholder="Поиск по номеру карты"
                value={searchNumber}
                onChange={e =>
                    setSearchNumber(e.target.value)
                }
            />

            <CardList cards={processedCards} onClose={closeCard} onDelete={deleteCard} onSort={handleSort}/>

        </div>

    );

}
