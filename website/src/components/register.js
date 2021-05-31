import React, { useState } from 'react';

const Register = () => {
    const [fullname, setFullname] = useState('');
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');


    async function onRegister(e) {
        e.preventDefault();

        var fullnameSplit = fullname.split(" ");

        console.log(fullnameSplit);

        const formData = new FormData();
        formData.append("name", fullnameSplit[0]);
        formData.append("last_name", fullnameSplit[1]);
        formData.append("age", 21);
        formData.append("nickname", username);
        formData.append("email", email);
        formData.append("password", password);

        await fetch('https://rt21-api.herokuapp.com/api/user/register', {
            method: 'POST',
            headers: {
                'X-API-Key': '04fca805-c486-4519-9bdb-7dd80733dfd1',
            },
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("HTTP status code " + response.status);
            } 
            return response.json();
        })
        .then(data => {
            console.log("success");

            setFullname("");
            setUsername("");
            setEmail("");
            setPassword("");

            console.log(data);

            window.location = '/login';
        }) 
        .catch((error) => {
            console.log("error: " + error);
        });
    }


    return (
        <div className="card w-75 m-auto mt-5">
            <p style={{color: 'green'}}><b>TODO:</b> if unssuccesful register show error on bottom</p>
            <div className="card-header text-center">
                <h3>Register</h3>
            </div>
            <div className="card-body">
                <form onSubmit={onRegister}>
                    <div className="input-group mb-3">
                        <div class="input-group-text"></div>
                        <input type="text" className="form-control" name="fullname" placeholder="Full name" required="" value={fullname} onChange={(e)=>{setFullname(e.target.value)}}/>
                    </div>
                    <div className="input-group mb-3">
                        <div class="input-group-text">📧</div>
                        <input type="text" className="form-control" name="email" placeholder="Email" required="" value={email} onChange={(e)=>{setEmail(e.target.value)}}/>
                    </div>
                    <div className="input-group mb-3">
                        <div class="input-group-text">👤</div>
                        <input type="text" className="form-control" name="username" placeholder="Username" required="" value={username} onChange={(e)=>{setUsername(e.target.value)}}/>
                    </div>
                    <div className="input-group mb-3">
                        <div className="input-group-text">🔒</div>
                        <input type="password" className="form-control" name="password" placeholder="Password" required="" value={password} onChange={(e)=>{setPassword(e.target.value)}}/>
                    </div>
                    <div className="text-center">
                        <input type="submit" className="btn btn-primary w-50" style={{fontSize: 17}} value="Register"/>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default Register;