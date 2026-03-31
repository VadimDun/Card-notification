import { useState, useEffect } from "react";

export default function CardForm({ onCreate, clients }) {

    const [cardNumber, setCardNumber] = useState("");
    const [issueDate, setIssueDate] = useState("");
    const [expDate, setExpDate] = useState("");
    const [clientId, setClientId] = useState("");

    function handleSubmit(e) {

        e.preventDefault();

        if (expDate <= issueDate) {

            alert(
                "Дата окончания должна быть позже даты выдачи"
            );

            return;
        }

        const card = {
            cardNumber,
            issueDate,
            expDate,
            clientId: parseInt(clientId)
        };

        onCreate(card);

        setCardNumber("");
        setIssueDate("");
        setExpDate("");
        setClientId("");

    }

    return (

        <form onSubmit={handleSubmit}>

            <input
                placeholder="Номер карты (16 цифр)"
                value={cardNumber}
                onChange={e => setCardNumber(e.target.value)}
                pattern="\d{16}"
                required
            />

            <input
                type="date"
                value={issueDate}
                onChange={e => setIssueDate(e.target.value)}
                required
            />

            <input
                type="date"
                value={expDate}
                onChange={e => setExpDate(e.target.value)}
                required
            />

            <select value={clientId} onChange={e => setClientId(e.target.value)} required>
                <option value="">Выберите клиента</option>
                {clients.map(client => (
                    <option key={client.id} value={client.id}>
                        {client.fullName} (ID: {client.id})
                    </option>
                ))}
            </select>

            <button type="submit" className="btn-submit">
                Добавить карту
            </button>

        </form>

    );

}
