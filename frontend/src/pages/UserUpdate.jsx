import React, { useState } from "react";
import styled from "styled-components";
import GenderDropdown from '../components/GenderDropdown'
import { RiArrowDropDownLine, RiArrowDropUpLine } from "react-icons/ri";

const Container = styled.div`
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 20px;
`

const Title = styled.h1`
    margin-bottom: 20px;
`

const Label = styled.span`
    color: #254ef8;
    font-weight: bolder;
    margin-bottom: 10px;
`

const Form = styled.div`

`

const InputWrapper = styled.div`
    margin-bottom: 1rem;
    display: flex;
    flex-direction: column;
`

const Input = styled.input`
    placeholder: ${(props) => props.placeholder};

`

function UserUpdateForm(props) {
    const [open, setOpen] = useState()
    
    return(
        <Container>
            <Title>
                Profile Settings
            </Title>
            <Form>
                <InputWrapper>
                    <Label>
                        Name
                    </Label>
                    <Input placeholder={'이름을 입력하세요'}/>
                </InputWrapper>
                <InputWrapper>
                    <Label>
                        Nickname
                    </Label>
                    <Input placeholder={'닉네임을 입력하세요'}/>
                </InputWrapper>
                <InputWrapper>
                    <Label>
                        Self-introdution
                    </Label>
                    <Input placeholder={'자기소개 문구를 입력하세요'}/>
                </InputWrapper>
                <InputWrapper>
                    <Label>
                        Gender
                    </Label>
                    <ul onClick={() => {setOpen(!open)}}>
                        {open ? <RiArrowDropUpLine /> : <RiArrowDropDownLine />}
                        {open && <GenderDropdown />}
                    </ul>
                </InputWrapper>
                <InputWrapper>
                    <Label>
                        Birth
                    </Label>

                </InputWrapper>
                <InputWrapper>
                    <Label>
                        Like genre
                    </Label>

                </InputWrapper>
                <InputWrapper>
                    <Label>
                        Position
                    </Label>
                    
                </InputWrapper>
                <InputWrapper>
                    <Label>
                        SNS
                    </Label>
                    
                </InputWrapper>
            </Form>
            {/* <Button>수정 완료</Button> */}
        </Container> 
    )
}

export default UserUpdateForm