import React, { useState } from 'react';

const Login = (props) => {
    const[email, setEmail] = useState('');
    const[password, setPassword] = useState('');

    const[errorMessage, setErrorMessage] = useState('');

    async function onLogin(e) {
        e.preventDefault();

        const formData = new FormData();
        formData.append("email", email);
        formData.append("password", password);

        await fetch('https://rt21-api.herokuapp.com/api/user/login', {
            method: 'POST',
            headers: {
                'X-API-Key': '04fca805-c486-4519-9bdb-7dd80733dfd1',
            },
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(response => {throw new Error(response.error)})
            }
            return response.json();
        })
        .then(data => {
            setEmail("");
            setPassword("");

            localStorage.setItem('userSessionID', JSON.stringify(data));

            window.location = '/';
        })
        .catch(error => {
            setErrorMessage('' + error);
        });
    }

    return (
        <div className="card w-75 m-auto mt-5">
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
            { errorMessage !== '' &&
                <div style={{background: '#f8d7da'}} className="card-footer border-danger">
                    <span style={{color: '#721c24'}}>{errorMessage}</span> 
                </div>
            }
        </div>
    )
}

export default Login;