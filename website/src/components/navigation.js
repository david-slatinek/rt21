import React from 'react';
import { Route, Switch, BrowserRouter as Router } from 'react-router-dom';

import Login from './login';
import Register from './register';
import Home from './home';
import Profile from './profile';

const Navigation = () => {

    function logOut() {
        localStorage.removeItem("userSessionID");
        
        window.location = '/';
    }

    return (
    <div>
        <Router>
            <nav className="navbar navbar-expand-lg navbar-light bg-light">
                <ul className="navbar-nav">
                    <li className="nav-item">
                       <a className="nav-link" href="/">Home</a>
                    </li>
                    <li className="nav-item">
                        {(localStorage.getItem("userSessionID") === null ? <a className="nav-link" href="/login">Login</a> : null)}
                    </li>
                    <li className="nav-item">
                        {(localStorage.getItem("userSessionID") === null ?  <a className="nav-link" href="/register">Register</a> : null)}
                    </li>
                    <li className="nav-item">
                        {(localStorage.getItem("userSessionID") !== null ? <a className="nav-link" href="/profile">Profile</a> : null)}
                    </li>
                    <li>
                        {(localStorage.getItem("userSessionID") !== null ? <button id="logoutButton" className="nav-link" onClick={logOut}>Log out</button> : null)}
                    </li>
                </ul>
            </nav>
        
            <Switch>
                <Route path="/" exact>
                    <Home/>
                </Route>
                <Route path="/login">
                    <Login user={"testing"}/>
                </Route>
                <Route path="/register">
                    <Register />
                </Route>
                <Route path="/profile">
                    <Profile user={"uporabnik"}/>
                </Route>
            </Switch>
        </Router>
    </div>
    )
}

export default Navigation;