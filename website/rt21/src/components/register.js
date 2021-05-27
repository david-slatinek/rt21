import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'

const Register = () => {
    return (
        <div className="card w-75 m-auto mt-5">
            <p style={{color: 'green'}}><b>TODO:</b> connect to API and add new user to database</p>
            <div className="card-header text-center">
                <h3>Register</h3>
            </div>
            <div className="card-body">
                <form>
                    <div className="input-group mb-3">
                        <div class="input-group-text"></div>
                        <input type="text" className="form-control" name="fullname" placeholder="Full name"/>
                    </div>
                    <div className="input-group mb-3">
                        <div class="input-group-text">📧</div>
                        <input type="text" className="form-control" name="email" placeholder="Email"/>
                    </div>
                    <div className="input-group mb-3">
                        <div class="input-group-text">👤</div>
                        <input type="text" className="form-control" name="username" placeholder="Username"/>
                    </div>
                    <div className="input-group mb-3">
                        <div className="input-group-text">🔒</div>
                        <input type="password" className="form-control" name="password" placeholder="Password"/>
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