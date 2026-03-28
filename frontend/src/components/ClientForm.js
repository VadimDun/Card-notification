import { useState } from "react";

export default function ClientForm({ onCreate }) {

    const [fullName, setFullName] = useState("");
    const [birthDate, setBirthDate] = useState("");
    const [email, setEmail] = useState("");

    function handleSubmit(e) {

        e.preventDefault();

        const client = {
            fullName,
            birthDate,
            email
        };

        onCreate(client);

        setFullName("");
        setBirthDate("");
        setEmail("");

    }

    return (

        <form onSubmit={handleSubmit}>

            <input
                placeholder="Полное имя клиента"
                value={fullName}
                onChange={e => setFullName(e.target.value)}
            />

            <input
                type="date"
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

    );
}
