import * as React from "react"
import { useState, useEffect } from "react"
import { Navigate, useLocation, Link } from "react-router-dom"


function delay(delayInMs: number) {
    return new Promise((resolve, reject) => {
        setTimeout(() => resolve(undefined), delayInMs)
    })
}

export async function authenticate(credentials): Promise<[string, string] | undefined> {

    const resp = await fetch("http://localhost:8080/login",
        {
            method: 'PUT',
            body: JSON.stringify(credentials),
            headers: { 'content-type': 'application/json;charset=UTF-8' }
        })
    const data = await resp.json()
    console.log(data.properties)
    return data.properties
}

export function Login() {
    console.log("Login")
    const [inputs, setInputs] = useState({
        username: "",
        password: "",
    })
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [error, setError] = useState("")
    const [redirect, setRedirect] = useState(false)
    const location = useLocation()

    function handleChange(ev: React.FormEvent<HTMLInputElement>) {
        const name = ev.currentTarget.name
        setInputs({ ...inputs, [name]: ev.currentTarget.value })
        setError("")
    }
    function handleSubmit(ev: React.FormEvent<HTMLFormElement>) {
        ev.preventDefault()
        setIsSubmitting(true)
        authenticate(inputs)
            .then(res => {
                setIsSubmitting(false)
                //if credentials are correct, the token shouldn't be undefined
                if (res && res["first"]) {
                    const token = res["first"]
                    console.log(res["first"])
                    const redirect = location.state?.source?.pathname || "/me"
                    //ssaving token to maintain session
                    sessionStorage.setItem("user_session", res["first"])
                    //saving username
                    sessionStorage.setItem("current_user", res["second"])
                    setRedirect(true)
                } else {
                    setError("Invalid username or password")
                }

            })
            .catch(error => {
                setIsSubmitting(false)
                setError("Invalid credentials")
            })


    }

    useEffect(() => {
        const timeoutId = setTimeout(() => {
            setError("");
        }, 3000);

        return () => {
            clearTimeout(timeoutId);
        };
    }, [error]);

    if (redirect) {
        return <Navigate to={location.state?.source?.pathname || "/me"} replace={true} />
    }
    return (

        <div>
            <div className="link-container">
                <Link className="link" to="/home">Home</Link>
                <Link className="link" to="/leaderboard">Leaderboard</Link>
            </div>

            <form onSubmit={handleSubmit}>
                <h3>Login</h3>

                <div>
                    <input placeholder="Username" id="username" type="text" name="username" value={inputs.username} onChange={handleChange} />
                </div>
                <div>
                    <input placeholder="Password" id="password" type="password" name="password" value={inputs.password} onChange={handleChange} />
                </div>
                <div>
                    <button className="form-button" type="submit">Login</button>

                </div>
                <p><Link to='/register'>Don't have an account?</Link></p>

                <div className="error">{error}</div>
            </form>
        </div>
    )
}