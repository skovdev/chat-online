import React, { useState } from "react";

import SockJS from "sockjs-client";
import { over } from "stompjs";

var stompClient = null

const ChatRoom = () => {

    const [publicChats, setPublicChats] = useState([]);
    const [privateChats, setPrivateChats] = useState(new Map());
    const [tab, setTab] = useState("CHATROOM");
    const [userData, setUserData] = useState({
        username: "",
        receiveName: "",
        connected: false,
        message: ""
    })

    const handleUserName = (event) => {
        const {value} = event.target;
        setUserData({...userData, "username": value})
    }

     const handleValue = (event) => {
        const { value, name } = event.target;
        setUserData({...userData, [name]:value})
    }

    const handleMessage = (event) => {
        const {value} = event.target;
        setUserData({...userData, "message": value})
    }

    const registerUser = () => {
        let Sock = new SockJS("http://localhost:8080/ws");
        stompClient = over(Sock);
        stompClient.connect({}, onConnected, onError);
    }

    const onConnected = () => {
        setUserData({...userData, "connected": true});
        stompClient.subscribe("/chatroom/public", onPublicMessageReceived);
        stompClient.subscribe("/user/" + userData.username + "/private", onPrivateMessageReceived);
        userJoin();
    }

    const userJoin = () => {

        let chatMessage = {
            senderName: userData.username,
            status: "JOIN"
        };
        stompClient.send("/app/message", {}, JSON.stringify(chatMessage));
    }

    const onError = (err) => {
        console.log(err)
    }

    const onPublicMessageReceived = (payload) => {

        let payloadData = JSON.parse(payload.body);
        
        switch (payloadData.status) {
            case "JOIN":
                if (privateChats.get(payloadData.senderName)) {
                    privateChats.get(payloadData.senderName).push(payloadData);
                    setPrivateChats(new Map(privateChats));
                }
                break;
            case "MESSAGE":
                publicChats.push(payloadData);
                setPublicChats([...publicChats]);
                break;
        }
    }

    const onPrivateMessageReceived = (payload) => {
        
        let payloadData = JSON.parse(payload);
        
        if (privateChats.get(payloadData.senderName)) {
            privateChats.get(payloadData.senderName).push(payloadData);
            setPrivateChats(new Map(privateChats));
        } else {
            let list = [];
            list.push(payloadData);
            privateChats.set(payloadData.senderName, list);
            setPrivateChats(new Map(privateChats));
        }
    }

    const sendPublicMessage = () => {

        if (stompClient) {
            let chatMessage = {
                senderName: userData.username,
                message: userData.message,
                status: "MESSAGE"
            };
            stompClient.send("/app/message", {}, JSON.stringify(chatMessage));
            setUserData({...userData, "message": ""});
        }
    }

    const sendPrivateMessage = () => {

        if (stompClient) {

            let chatMessage = {
                senderName: userData.username,
                message: userData.message,
                status: "MESSAGE"
            };

            if (userData.username !== tab) {
                privateChats.set(tab).push(chatMessage);
                setPrivateChats(new Map(privateChats));
            }

            stompClient.send("/app/private-message", {}, JSON.stringify(chatMessage));
            setUserData({...userData, "message": ""});

        }
    }

    return (
        <div className="container">
            {userData.connected?
                <div className="chat-box">
                    <div className="member-list">
                           <ul>
                            <li onClick={() => {setTab("CHATROOM")}} className={`member ${tab === "CHATROOM" && "active"}`}>Chatroom</li>
                            {[...privateChats.keys()].map((name, index) => (
                                <li onClick={() => {setTab(name)}} className={`member ${tab === "CHATROOM" && "active"}`} key={index}>
                                    {name}
                                </li>
                            ))}
                        </ul>
                    </div>
                    {tab === "CHATROOM" && <div className="chat-content">
                        <ul className="chat-messages">
                            {publicChats.map((chat, index) => (
                                <li className="message" key={index}>
                                    {chat.senderName !== userData.username && <div className="avatar">{chat.senderName}</div>}
                                    <div className="message-data">{chat.message}</div>
                                    {chat.senderName === userData.username && <div className="avatar self">{chat.senderName}</div>}
                                </li>
                            ))}
                        </ul>
                        <div className="send-message">
                            <input type="text" className="input-message" placeholder="Enter public message" value={userData.message} 
                                         onChange={handleMessage} />
                            <button type="button" className="send-button" onClick={sendPublicMessage}>Send</button>
                        </div>
                    </div>}
                    {tab !== "CHATROOM" && <div className="chat-content">
                        <ul className="chat-messages">
                            {[...privateChats.get(tab)].map((chat, index) => (
                                <li className="message" key={index}>
                                    {chat.senderName !== userData.username && <div className="avatar">{chat.senderName}</div>}
                                    <div className="message-data">{chat.message}</div>
                                    {chat.senderName === userData.username && <div className="avatar self">{chat.senderName}</div>}
                                </li>
                            ))}
                        </ul>
                        <div className="send-message">
                            <input type="text" className="input-message" placeholder={`Enter public message for ${tab}`} value={userData.message} 
                                         onChange={handleValue} />
                            <button type="button" className="send-button" onClick={sendPrivateMessage}>Send</button>
                        </div>
                    </div>}
                </div>
                :
                <div className="register">
                    <input id="username" placeholder="Enter the username" value={userData.username} onChange={handleUserName} />
                    <button type="button" onClick={registerUser}>
                        Connect
                    </button>
                </div>
            }
        </div>
    )
}

export default ChatRoom