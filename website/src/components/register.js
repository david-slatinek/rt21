import React, {useState} from 'react';
import config from '../config'

const Register = () => {
    const [fullname, setFullname] = useState('');
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [age, setAge] = useState('');

    const [errorMessage, setErrorMessage] = useState('');


    async function onRegister(e) {
        e.preventDefault();

        var fullnameSplit = fullname.split(" ");

        var api_key_val = config.API_KEY_VALUE;
        const formData = new FormData();
        formData.append("name", fullnameSplit[0]);
        formData.append("last_name", fullnameSplit[1]);
        formData.append("age", age);
        formData.append("nickname", username);
        formData.append("email", email);
        formData.append("password", password);

        await fetch('https://rt21-api.herokuapp.com/api/user/register', {
            method: 'POST',
            headers: {
                'X-API-Key': api_key_val,
            },
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(response => {
                        throw new Error(response.error)
                    })
                }
                return response.json();
            })
            .then(data => {
                console.log("success");

                setFullname("");
                setUsername("");
                setEmail("");
                setPassword("");
                setAge("");

                console.log(data);

                window.location = '/login';
            })
            .catch((error) => {
                setErrorMessage('' + error);
            });
    }


    return (
        <div className="card w-75 m-auto mt-5">
            <div className="card-header text-center">
                <h3>Register</h3>
            </div>
            <div className="card-body">
                <form onSubmit={onRegister}>
                    <div className="input-group mb-3">
                        <div className="input-group-text"><i className="fas fa-user-edit"/></div>
                        <input type="text" className="form-control" name="fullname" placeholder="Name and lastname" required
                               value={fullname} onChange={(e) => {
                            setFullname(e.target.value)
                        }}/>
                    </div>

                    <div className="input-group mb-3">
                        <div className="input-group-text"><i className="fas fa-envelope"/></div>
                        <input type="email" className="form-control" name="email" placeholder="Email" required
                               value={email} onChange={(e) => {
                            setEmail(e.target.value)
                        }}/>
                    </div>

                    <div className="input-group mb-3">
                        <div className="input-group-text"><i className="fas fa-user-alt"/></div>
                        <input type="text" className="form-control" name="username" placeholder="Username" required
                               value={username} onChange={(e) => {
                            setUsername(e.target.value)
                        }}/>
                    </div>

                    <div className="input-group mb-3">
                        <div className="input-group-text"><i className="far fa-calendar"/></div>
                        <input type="number" className="form-control" name="age" placeholder="Age"
                               required value={age} onChange={(e) => {
                            setAge(e.target.value)
                        }}/>
                    </div>


                    <div className="input-group mb-3">
                        <div className="input-group-text"><i className="fas fa-key"/></div>
                        <input type="password" className="form-control" name="password" placeholder="Password"
                               required value={password} onChange={(e) => {
                            setPassword(e.target.value)
                        }}/>
                    </div>

                    <div className="text-center">
                        <input type="submit" className="btn btn-dark w-50" style={{fontSize: 17}} value="Register"/>
                    </div>
                </form>
            </div>
            {errorMessage !== '' &&
            <div style={{background: '#f8d7da'}} className="card-footer border-danger">
                <span style={{color: '#721c24'}}>{errorMessage}</span>
            </div>
            }
        </div>
    )
}

export default Register;