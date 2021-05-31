import React from 'react';
import { Route, Switch, BrowserRouter as Router } from 'react-router-dom';

import Login from './login';
import Register from './register';
import Home from './home';
import Profile from './profile';

const Navigation = () => {
    return (
    <div>
        <Router>
            <p style={{color: 'green'}}><b>TODO:</b> if user is logged in show HOME, PROFILE and LOG OUT else the only thing that users see is login and register page</p>
            <nav className="navbar navbar-expand-lg navbar-light bg-light">
                <ul className="navbar-nav">
                    <li className="nav-item">
                        {/* <Link to="/">Home</Link> */}
                        <a className="nav-link" href="/">Home</a>
                    </li>
                    <li className="nav-item">
                        {/* <Link to="/login">Login</Link> */}
                        <a className="nav-link" href="/login">Login</a>
                    </li>
                    <li className="nav-item">
                        {/* <Link to="/register">Register</Link> */}
                        <a className="nav-link" href="/register">Register</a>
                    </li>
                    <li className="nav-item">
                        <a className="nav-link" href="/profile">Profile</a>
                    </li>
                    <li>
                        <a className="nav-link" href="#">Log out</a>
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