import React, { useState } from 'react';

const Login = (props) => {
    const[email, setEmail] = useState('');
    const[password, setPassword] = useState('');


    async function onLogin(e) {
        e.preventDefault();

        await fetch('https://rt21-api.herokuapp.com/api/user/login', {
            method: 'POST',
            withCredentials: true,
            credentials: 'include',
            headers: {
              'Authorization': 'Bearer rt21-api',
              'X-API-Key': '04fca805-c486-4519-9bdb-7dd80733dfd1',
              'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("HTTP status code " + response.status);
            }
            return response.json();
        })
        .then(data => {
            console.log("success");

            setEmail("");
            setPassword("");

            console.log(data);

            window.location = '/';
        })
        .catch((error) => {
            console.log("error: " + error);
        });
    }

    return (
        <div className="card w-75 m-auto mt-5">
            <p style={{color: 'green'}}><b>TODO:</b> connect to API and check if user credentials are ok</p>
            <div className="card-header text-center">
                <h3>Login</h3>
            </div>
            <div className="card-body">
                <form onSubmit={onLogin}>
                    <div className="input-group mb-3">
                        <div class="input-group-text">📧</div>
                        <input type="text" className="form-control" name="email" placeholder="Email" required="" value={email} onChange={(e) => {setEmail(e.target.value)}}/>
                    </div>
                    <div className="input-group mb-3">
                        <div className="input-group-text">🔒</div>
                        <input type="password" className="form-control" name="password" placeholder="Password" required="" value={password} onChange={(e) => {setPassword(e.target.value)}}/>
                    </div>
                    <div className="text-center">
                        <input type="submit" className="btn btn-primary w-50" style={{fontSize: 17}} value="Login"/>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default Login;